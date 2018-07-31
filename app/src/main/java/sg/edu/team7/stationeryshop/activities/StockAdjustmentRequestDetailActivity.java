package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
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
import sg.edu.team7.stationeryshop.fragments.StockAdjustmentRequestsFragment;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequestDetail;
import sg.edu.team7.stationeryshop.util.JSONParser;
import sg.edu.team7.stationeryshop.util.StockAdjustmentRequestDetailAdapter;

public class StockAdjustmentRequestDetailActivity extends AppCompatActivity {

    private Map stockAdjustment;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_adjustment_request_detail);

        // Set Title
        stockAdjustment = (Map) getIntent().getSerializableExtra("stockAdjustment");
        getSupportActionBar().setTitle("Stock Adjustment - " + stockAdjustment.get("stockAdjustmentId").toString());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Disbursement Details
        TextInputEditText dateRequested = findViewById(R.id.detail_date);
        TextInputEditText employeeName = findViewById(R.id.detail_employee);
        TextInputEditText status = findViewById(R.id.detail_status);
        TextInputEditText remarks = findViewById(R.id.detail_remarks);

        dateRequested.setText(stockAdjustment.get("requestedDate").toString());
        employeeName.setText(stockAdjustment.get("requestorName").toString());
        status.setText(stockAdjustment.get("status").toString());
        remarks.setText(stockAdjustment.get("remarks").toString());

        // Get Item Details
        List genericList = (List) stockAdjustment.get("stockAdjustmentRequestDetails");
        List<StockAdjustmentRequestDetail> stockAdjustmentDetails = new ArrayList<>();
//
        genericList.forEach(x -> stockAdjustmentDetails.add(new StockAdjustmentRequestDetail(
                ((Map) x).get("itemCode").toString(),
                ((Map) x).get("itemName").toString(),
                ((Map) x).get("originalQuantity").toString(),
                ((Map) x).get("afterQuantity").toString(),
                ((Map) x).get("reason").toString()
        )));

        // Initialize RecyclerView
        RecyclerView mRecyclerView = findViewById(R.id.detail_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(new StockAdjustmentRequestDetailAdapter(stockAdjustmentDetails, this));

        // Initialize loading UI
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize Approve Button
        Button approveButton = findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject stockAdjustmentId = new JSONObject();
                        try {
                            stockAdjustmentId.put("StockAdjustmentId", StockAdjustmentRequestDetailActivity.this.stockAdjustment.get("stockAdjustmentId").toString());
                            stockAdjustmentId.put("Email", StockAdjustmentRequestDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                            String message = JSONParser.postStream(
                                    StockAdjustmentRequestDetailActivity.this.getString(R.string.default_hostname) + "/api/stockadjustment/supervisor/approve",
                                    stockAdjustmentId.toString()
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
                        StockAdjustmentRequestDetailActivity.this.progressDialog.setTitle("Approving stock adjustment");
                        StockAdjustmentRequestDetailActivity.this.progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        StockAdjustmentRequestDetailActivity.this.progressDialog.dismiss();
                        Toast.makeText(StockAdjustmentRequestDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        StockAdjustmentRequestDetailActivity.this.finish();
                        new StockAdjustmentRequestsFragment.UpdateStockAdjustment().execute();
                    }
                }.execute();
            }
        });

        // Initialize Reject Button
        Button rejectButton = findViewById(R.id.reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject stockAdjustmentId = new JSONObject();
                        try {
                            stockAdjustmentId.put("StockAdjustmentId", StockAdjustmentRequestDetailActivity.this.stockAdjustment.get("stockAdjustmentId").toString());
                            stockAdjustmentId.put("Email", StockAdjustmentRequestDetailActivity.this.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                            String message = JSONParser.postStream(
                                    StockAdjustmentRequestDetailActivity.this.getString(R.string.default_hostname) + "/api/stockadjustment/supervisor/reject",
                                    stockAdjustmentId.toString()
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
                        StockAdjustmentRequestDetailActivity.this.progressDialog.setTitle("Rejecting stock adjustment");
                        StockAdjustmentRequestDetailActivity.this.progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        StockAdjustmentRequestDetailActivity.this.progressDialog.dismiss();
                        Toast.makeText(StockAdjustmentRequestDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        StockAdjustmentRequestDetailActivity.this.finish();
                        new StockAdjustmentRequestsFragment.UpdateStockAdjustment().execute();
                    }
                }.execute();
            }
        });

        if (!stockAdjustment.get("status").toString().equals("Pending Approval")) {
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
