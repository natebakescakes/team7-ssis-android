package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;
import sg.edu.team7.stationeryshop.util.RetrievalDetailByDeptAdapter;

public class RetrievalDetailByDeptActivity extends AppCompatActivity {

    private Map retrieval;
    private ProgressDialog progressDialog;
    private List<RetrievalDetailByDept> retrievalDetails;
    private String itemCode;
    private FloatingActionButton editButton;
    private FloatingActionButton confirmButton;
    private RetrievalDetailByDeptAdapter mAdapter;

    public String getItemCode() {
        return itemCode;
    }

    public Map getRetrieval() {
        return retrieval;
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

        // Get Retrieval
        retrieval = (Map) getIntent().getSerializableExtra("retrieval");

        // Get Item Details
        List genericList = (List) retrieval.get("retrievalDetails");
        retrievalDetails = new ArrayList<>();

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
        mAdapter = new RetrievalDetailByDeptAdapter(
                retrievalDetails.stream().filter(x -> x.get("itemCode").toString().equals(this.itemCode)).collect(Collectors.toList()),
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