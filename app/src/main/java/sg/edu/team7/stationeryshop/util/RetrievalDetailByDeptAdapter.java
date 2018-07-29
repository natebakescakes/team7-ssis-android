package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.RetrievalDetailByDeptActivity;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;

public class RetrievalDetailByDeptAdapter extends RecyclerView.Adapter<RetrievalDetailByDeptAdapter.ViewHolder> {
    private final RetrievalDetailByDeptActivity activity;
    private final List<RetrievalDetailByDept> retrievalDetails;

    private List<RetrievalDetailByDept> retrievalDetailByDepts;
    private List<ViewHolder> views;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RetrievalDetailByDeptAdapter(List<RetrievalDetailByDept> retrievalDetails, RetrievalDetailByDeptActivity activity) {
        this.retrievalDetails = retrievalDetails;
        this.activity = activity;

        this.views = new ArrayList<>();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RetrievalDetailByDeptAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RetrievalDetailByDeptAdapter.ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.retrieval_detail_by_dept_list_row, parent, false)
        );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RetrievalDetailByDeptAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.department.setText(retrievalDetails.get(position).get("department").toString());
        viewHolder.departmentCode.setText(retrievalDetails.get(position).get("departmentCode").toString());

        viewHolder.planQuantity.setText(retrievalDetails.get(position).get("planQuantity").toString());
        viewHolder.planQuantity.setHint("Plan - " + retrievalDetails.get(position).get("uom").toString());

        viewHolder.actualQuantity.setText(retrievalDetails.get(position).get("actualQuantity").toString());
        viewHolder.actualQuantity.setHint("Actual - " + retrievalDetails.get(position).get("uom").toString());

        views.add(viewHolder);
    }

    public void editMode() {
        views.forEach(view -> {
            view.actualQuantity.setEnabled(true);
        });

        activity.getEditButton().hide();
        activity.getConfirmButton().show();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return retrievalDetails != null ? retrievalDetails.size() : 0;
    }

    public void updateActualQuantity() {
        JSONObject retrieval = new JSONObject();

        try {
            retrieval.put("RetrievalId", "");
            retrieval.put("Email", "");

            JSONArray retrievalDetails = new JSONArray();

            views.forEach(view -> {
                JSONObject retrievalDetail = new JSONObject();
                try {
                    retrievalDetail.put("Department", "");
                    retrievalDetail.put("ItemCode", "");
                    retrievalDetail.put("ActualQuantity", "");
                    retrieval.put("RetrievalDetails", retrievalDetails);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("RETRIEVAL", retrieval.toString());
        activity.finish();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView department;
        public TextView departmentCode;
        public TextInputEditText planQuantity;
        public TextInputEditText actualQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            department = itemView.findViewById(R.id.dept_name);
            departmentCode = itemView.findViewById(R.id.dept_code);
            planQuantity = itemView.findViewById(R.id.plan_quantity_value);
            actualQuantity = itemView.findViewById(R.id.actual_quantity_value);
        }
    }
}
