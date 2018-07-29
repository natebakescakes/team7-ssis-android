package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;
import sg.edu.team7.stationeryshop.util.JSONParser;
import sg.edu.team7.stationeryshop.util.RetrievalDetailAdapter;

public class RetrievalDetailActivity extends AppCompatActivity {

    private Map retrieval;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieval_detail);

        // Set Title
        retrieval = (Map) getIntent().getSerializableExtra("retrieval");
        getSupportActionBar().setTitle("Retrieval - " + retrieval.get("retrievalId").toString());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Get Item Details
        List genericList = (List) retrieval.get("retrievalDetails");
        List<RetrievalDetailByDept> retrievalDetails = new ArrayList<>();
//
        genericList.forEach(x -> retrievalDetails.add(new RetrievalDetailByDept(
                ((Map) x).get("department").toString(),
                ((Map) x).get("departmentCode").toString(),
                ((Map) x).get("itemCode").toString(),
                ((Map) x).get("itemName").toString(),
                ((Map) x).get("bin").toString(),
                ((Map) x).get("uom").toString(),
                ((Map) x).get("status").toString(),
                Integer.parseInt(((Map) x).get("planQuantity").toString()),
                Integer.parseInt(((Map) x).get("actualQuantity").toString())
        )));

        // Initialize RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.retrieval_detail_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new RetrievalDetailAdapter(retrieval, this));

        // Initialize loading UI
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize Confirm Retrieval Button
        Button confirmButton = findViewById(R.id.confirm_retrieval);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject retrievalId = new JSONObject();
                        try {
                            retrievalId.put("RetrievalId", RetrievalDetailActivity.this.retrieval.get("retrievalId").toString());
                            retrievalId.put("Email", RetrievalDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                            String message = JSONParser.postStream(
                                    RetrievalDetailActivity.this.getString(R.string.default_hostname) + "/api/stockadjustment/supervisor/reject",
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
                        RetrievalDetailActivity.this.progressDialog.setTitle("Rejecting stock adjustment");
                        RetrievalDetailActivity.this.progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        RetrievalDetailActivity.this.progressDialog.dismiss();
                        Toast.makeText(RetrievalDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        RetrievalDetailActivity.this.finish();
                    }
                }.execute();
            }
        });

        if (retrieval.get("status").toString().equals("Retrieved"))
            confirmButton.setEnabled(false);
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
