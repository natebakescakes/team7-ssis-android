package sg.edu.team7.stationeryshop.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.fragments.ApproveRequisitionDialogFragment;
import sg.edu.team7.stationeryshop.fragments.RejectRequisitionDialogFragment;
import sg.edu.team7.stationeryshop.models.RequisitionDetail;
import sg.edu.team7.stationeryshop.util.RequisitionDetailAdapter;

public class RequisitionDetailActivity extends AppCompatActivity {

    public static ProgressDialog progressDialog = null;
    public static Map requisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition_detail);

        // Set Title
        requisition = (Map) getIntent().getSerializableExtra("requisition");
        getSupportActionBar().setTitle("Requisition - " + requisition.get("requisitionId").toString());
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Set Disbursement Details
        TextInputEditText dateRequested = findViewById(R.id.detail_date);
        TextInputEditText employeeName = findViewById(R.id.detail_employee);
        TextInputEditText status = findViewById(R.id.detail_status);
        TextInputEditText remarks = findViewById(R.id.detail_remarks);
        TextInputEditText headRemarks = findViewById(R.id.detail_head_remarks);
        TextInputLayout headRemarksLayout = findViewById(R.id.detail_layout_head_remarks);

        dateRequested.setText(requisition.get("requestedDate").toString());
        employeeName.setText(requisition.get("requestorName").toString());
        status.setText(requisition.get("status").toString());
        remarks.setText(requisition.get("remarks").toString());
        if (!requisition.get("status").toString().equals("Rejected") &&
                !requisition.get("status").toString().equals("Approved")) {
            headRemarks.setVisibility(View.GONE);
        } else if (requisition.get("status").toString().equals("Rejected")) {
            headRemarksLayout.setHint(getString(R.string.label_rejection_remarks));
            headRemarks.setText(requisition.get("headRemarks").toString());
        } else {
            headRemarksLayout.setHint(getString(R.string.label_approval_remarks));
            headRemarks.setText(requisition.get("headRemarks").toString());
        }

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
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Initialize Approve FAB
        Button approveButton = findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showApproveDialog("Approving " + requisition.get("requisitionId").toString());
            }
        });

        // Initialize Approve FAB
        Button rejectButton = findViewById(R.id.reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRejectDialog("Rejecting " + requisition.get("requisitionId").toString());
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

    private void showRejectDialog(String title) {
        FragmentManager fm = getSupportFragmentManager();
        RejectRequisitionDialogFragment rejectRequisitionDialogFragment = RejectRequisitionDialogFragment.newInstance(title);
        rejectRequisitionDialogFragment.show(fm, "fragment_edit_name");
    }

    private void showApproveDialog(String title) {
        FragmentManager fm = getSupportFragmentManager();
        ApproveRequisitionDialogFragment approveRequisitionDialogFragment = ApproveRequisitionDialogFragment.newInstance(title);
        approveRequisitionDialogFragment.show(fm, "fragment_edit_name");
    }
}
