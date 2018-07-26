package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.Requisition;


public class RequisitionAdapter extends RecyclerView.Adapter<RequisitionAdapter.ViewHolder> {
    private List<Requisition> requisitions;
    private OnItemClickListener onItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RequisitionAdapter(List<Requisition> requisitions, OnItemClickListener onItemClickListener) {
        this.requisitions = requisitions;
        this.onItemClickListener = onItemClickListener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RequisitionAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.requisitionId.setText(requisitions.get(position).get("requisitionId").toString());
        viewHolder.status.setText(requisitions.get(position).get("status").toString());
        viewHolder.requestedDate.setText(requisitions.get(position).get("requestedDate").toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RequisitionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.requisition_list_row, parent, false)
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return requisitions != null ? requisitions.size() : 0;
    }

    public void update(List<Requisition> requisitions) {
        this.requisitions.clear();
        this.requisitions.addAll(requisitions);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView requisitionId;
        public TextView status;
        public TextView requestedDate;

        public ViewHolder(View itemView) {
            super(itemView);
            requisitionId = itemView.findViewById(R.id.requisition_id);
            status = itemView.findViewById(R.id.requisition_status);
            requestedDate = itemView.findViewById(R.id.requisition_date);
        }
    }
}