package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;
import sg.edu.team7.stationeryshop.util.RetrievalDetailByDeptAdapter;

public class RetrievalDetailByDeptActivity extends AppCompatActivity {

    private String retrievalId;
    private ProgressDialog progressDialog;
    private String itemCode;
    private FloatingActionButton editButton;
    private FloatingActionButton confirmButton;
    private RetrievalDetailByDeptAdapter mAdapter;
    private List<RetrievalDetailByDept> retrievalDetailByDepts;

    public String getRetrievalId() {
        return retrievalId;
    }

    public String getItemCode() {
        return itemCode;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }

    public FloatingActionButton getConfirmButton() {
        return confirmButton;
    }

    public FloatingActionButton getEditButton() {
        return editButton;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieval_detail_by_dept);

        // Get RetrievalId
        retrievalId = getIntent().getStringExtra("retrievalId");

        // Set Title
        itemCode = getIntent().getStringExtra("itemCode");
        getSupportActionBar().setTitle("Retrieval Detail - " + getIntent().getStringExtra("itemCode"));
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Initialize RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.retrieval_detail_by_dept_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RetrievalDetailByDeptAdapter(retrievalId,
                new ArrayList<>(),
                this);
        mRecyclerView.setAdapter(mAdapter);

        // Initialize loading UI
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Set Edit Button
        editButton = findViewById(R.id.edit_fab);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.editMode();
            }
        });

        // Set Confirm Button
        confirmButton = findViewById(R.id.confirm_fab);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.updateActualQuantity();
            }
        });

        new UpdateRetrievalByDeptActivity().execute();
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

    public class UpdateRetrievalByDeptActivity extends AsyncTask<Void, Void, List<RetrievalDetailByDept>> {
        @Override
        protected List<RetrievalDetailByDept> doInBackground(Void... voids) {
            try {
                return RetrievalDetailByDept.findRetrieval(RetrievalDetailByDeptActivity.this.retrievalId);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle("Loading Retrieval Breakdown Details");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<RetrievalDetailByDept> retrievalDetailByDepts) {
            progressDialog.hide();
            if (retrievalDetailByDepts != null) {
                RetrievalDetailByDeptActivity.this.retrievalDetailByDepts = retrievalDetailByDepts;
                RetrievalDetailByDeptActivity.this.mAdapter.update(retrievalDetailByDepts.stream().filter(x -> x.get("itemCode").toString().equals(RetrievalDetailByDeptActivity.this.itemCode)).collect(Collectors.toList()));
                if (RetrievalDetailByDeptActivity.this.retrievalDetailByDepts.stream().findFirst().get().get("status").equals("Picked"))
                    editButton.hide();

            }
        }
    }
}