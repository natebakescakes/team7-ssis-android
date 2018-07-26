package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.Disbursement;


public class DisbursementAdapter extends RecyclerView.Adapter<DisbursementAdapter.ViewHolder> {
    private List<Disbursement> disbursements;
    private OnItemClickListener onItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DisbursementAdapter(List<Disbursement> disbursements, OnItemClickListener onItemClickListener) {
        this.disbursements = disbursements;
        this.onItemClickListener = onItemClickListener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull DisbursementAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.disbursementId.setText(disbursements.get(position).get("disbursementId").toString());
        viewHolder.department.setText(disbursements.get(position).get("department").toString());
        viewHolder.createdDate.setText(disbursements.get(position).get("createdDate").toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisbursementAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.disbursement_list_row, parent, false)
        );
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return disbursements != null ? disbursements.size() : 0;
    }

    public void update(List<Disbursement> disbursements) {
        this.disbursements.clear();
        this.disbursements.addAll(disbursements);
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView disbursementId;
        public TextView department;
        public TextView createdDate;

        public ViewHolder(View itemView) {
            super(itemView);
            disbursementId = itemView.findViewById(R.id.disbursement_id);
            department = itemView.findViewById(R.id.disbursement_department);
            createdDate = itemView.findViewById(R.id.disbursement_date);
        }
    }
}