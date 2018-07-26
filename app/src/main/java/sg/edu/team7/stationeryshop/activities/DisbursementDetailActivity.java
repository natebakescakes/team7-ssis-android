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
import sg.edu.team7.stationeryshop.models.DisbursementDetail;
import sg.edu.team7.stationeryshop.util.DisbursementDetailAdapter;
import sg.edu.team7.stationeryshop.util.JSONParser;

public class DisbursementDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disbursement_detail);

        // Set Title
        Map disbursement = (Map) getIntent().getSerializableExtra("disbursement");
        getSupportActionBar().setTitle("Disbursement - " + disbursement.get("disbursementId").toString());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Disbursement Details
        TextInputEditText createdDate = findViewById(R.id.detail_date);
        TextInputEditText department = findViewById(R.id.detail_department);
        TextInputEditText collectionPoint = findViewById(R.id.detail_collection_point);
        TextInputEditText status = findViewById(R.id.detail_status);

        createdDate.setText(disbursement.get("createdDate").toString());
        department.setText(disbursement.get("department").toString());
        collectionPoint.setText(disbursement.get("collectionPoint").toString());
        status.setText(disbursement.get("status").toString());

        // Get Item Details
        List genericList = (List) disbursement.get("disbursementDetails");
        List<DisbursementDetail> disbursementDetails = new ArrayList<>();

        genericList.forEach(x -> disbursementDetails.add(new DisbursementDetail(
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
        mRecyclerView.setAdapter(new DisbursementDetailAdapter(disbursementDetails));

        // Initialize loading UI
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Confirming Collection");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize FAB
        FloatingActionButton collectButton = findViewById(R.id.collect_delivery_fab);
        collectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject disbursementId = new JSONObject();
                        try {
                            disbursementId.put("DisbursementId", disbursement.get("disbursementId").toString());

                            String message = JSONParser.postStream(
                                    MainActivity.getContext().getString(R.string.default_hostname) + "/api/disbursement/collect",
                                    disbursementId.toString()
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
                        progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        progressDialog.dismiss();
                        Toast.makeText(DisbursementDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }.execute();
            }
        });

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
