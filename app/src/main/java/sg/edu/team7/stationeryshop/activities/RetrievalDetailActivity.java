package sg.edu.team7.stationeryshop.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.fragments.StationeryRetrievalFragment;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;
import sg.edu.team7.stationeryshop.util.JSONParser;
import sg.edu.team7.stationeryshop.util.RetrievalDetailAdapter;

public class RetrievalDetailActivity extends AppCompatActivity {

    private static List<RetrievalDetailByDept> retrievalDetailByDepts;
    private static RetrievalDetailAdapter mAdapter;
    private static Button confirmButton;
    private static ProgressDialog progressDialog;
    private String retrievalId;

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieval_detail);

        // Set Title
        retrievalId = getIntent().getStringExtra("retrievalId");
        getSupportActionBar().setTitle("Retrieval - " + getIntent().getStringExtra("retrievalId"));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new UpdateRetrievalDetail(getIntent().getStringExtra("retrievalId"));

        // Initialize RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.retrieval_detail_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        retrievalDetailByDepts = new ArrayList<>();
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RetrievalDetailAdapter(retrievalId, retrievalDetailByDepts, this);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize loading UI
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize Confirm Retrieval Button
        confirmButton = findViewById(R.id.confirm_retrieval);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RetrievalDetailActivity.this);
                builder.setTitle("Confirm Retrieval");
                builder.setMessage("Are you sure you want to Confirm Retrieval?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                JSONObject retrievalId = new JSONObject();
                                try {
                                    retrievalId.put("RetrievalId", RetrievalDetailActivity.this.retrievalId);
                                    retrievalId.put("Email", RetrievalDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                                    String message = JSONParser.postStream(
                                            RetrievalDetailActivity.this.getString(R.string.default_hostname) + "/api/retrieval/confirm",
                                            retrievalId.toString()
                                    );

                                    JSONObject result = new JSONObject(message);

                                    return result.getString("Message");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                return "Unknown error";
                            }

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                progressDialog.setTitle("Confirming retrieval");
                                progressDialog.show();
                            }

                            @Override
                            protected void onPostExecute(String message) {
                                progressDialog.dismiss();
                                Toast.makeText(RetrievalDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                                new StationeryRetrievalFragment.UpdateRetrievals().execute();
                                RetrievalDetailActivity.this.finish();
                            }
                        }.execute();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        new UpdateRetrievalDetail(retrievalId).execute();

//        Log.i("RETRIEVALDETAIL", retrievalDetailByDepts.stream().findFirst().get().toString());
//        if (retrievalDetailByDepts.stream().findFirst().get().get("retrievalStatus").toString().equals("Retrieved"))
//            confirmButton.setEnabled(false);
    }


    public static class UpdateRetrievalDetail extends AsyncTask<Void, Void, List<RetrievalDetailByDept>> {
        private final String retrievalId;

        public UpdateRetrievalDetail(String retrievalId) {
            this.retrievalId = retrievalId;
        }

        @Override
        protected List<RetrievalDetailByDept> doInBackground(Void... voids) {
            try {
                return RetrievalDetailByDept.findRetrieval(this.retrievalId);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RetrievalDetailActivity.progressDialog.setTitle("Loading Retrieval Details...");
            RetrievalDetailActivity.progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<RetrievalDetailByDept> retrievalDetailByDepts) {
            RetrievalDetailActivity.progressDialog.hide();
            if (retrievalDetailByDepts != null) {
                RetrievalDetailActivity.retrievalDetailByDepts = retrievalDetailByDepts;
                Log.i("RETRIEVALDETAIL", retrievalDetailByDepts.toString());
                RetrievalDetailActivity.mAdapter.update(retrievalDetailByDepts);
                if (retrievalDetailByDepts.stream().findFirst().get().get("retrievalStatus").toString().equals("Retrieved"))
                    RetrievalDetailActivity.confirmButton.setEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return true;
        }
    }
}
