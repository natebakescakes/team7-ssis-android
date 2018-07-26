package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.RequisitionDetail;
import sg.edu.team7.stationeryshop.util.JSONParser;
import sg.edu.team7.stationeryshop.util.RequisitionDetailAdapter;

public class RequisitionDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition_detail);

        // Set Title
        Map requisition = (Map) getIntent().getSerializableExtra("requisition");
        getSupportActionBar().setTitle("Requisition - " + requisition.get("requisitionId").toString());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Disbursement Details
        TextInputEditText dateRequested = findViewById(R.id.detail_date);
        TextInputEditText employeeName = findViewById(R.id.detail_employee);
        TextInputEditText status = findViewById(R.id.detail_status);

        dateRequested.setText(requisition.get("requestedDate").toString());
        employeeName.setText(requisition.get("requestorName").toString());
        status.setText(requisition.get("status").toString());

        // Get Item Details
        List genericList = (List) requisition.get("requisitionDetails");
        List<RequisitionDetail> requisitionDetails = new ArrayList<>();

        genericList.forEach(x -> requisitionDetails.add(new RequisitionDetail(
                ((Map) x).get("itemCode").toString(),
                ((Map) x).get("itemName").toString(),
                Integer.parseInt(((Map) x).get("quantity").toString()),
                ((Map) x).get("uom").toString()
        )));

        // Initialize RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.detail_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new RequisitionDetailAdapter(requisitionDetails));

        // Initialize loading UI
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize Approve FAB
        FloatingActionButton approveButton = findViewById(R.id.approve_fab);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject requisitionId = new JSONObject();
                        try {
                            requisitionId.put("RequisitionId", requisition.get("requisitionId").toString());
                            requisitionId.put("Email", RequisitionDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                            String message = JSONParser.postStream(
                                    MainActivity.getContext().getString(R.string.default_hostname) + "/api/requisition/approve",
                                    requisitionId.toString()
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
                        progressDialog.setTitle("Approving requisition");
                        progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(RequisitionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute();
            }
        });

        // Initialize Approve FAB
        FloatingActionButton rejectButton = findViewById(R.id.reject_fab);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject requisitionId = new JSONObject();
                        try {
                            requisitionId.put("RequisitionId", requisition.get("requisitionId").toString());
                            requisitionId.put("Email", RequisitionDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                            String message = JSONParser.postStream(
                                    MainActivity.getContext().getString(R.string.default_hostname) + "/api/requisition/reject",
                                    requisitionId.toString()
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
                        progressDialog.setTitle("Rejecting requisition");
                        progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(RequisitionDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute();
            }
        });

        if (requisition.get("status").toString().equals("Approved") ||
                requisition.get("status").toString().equals("Rejected")) {
            approveButton.setEnabled(false);
            rejectButton.setEnabled(false);
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
