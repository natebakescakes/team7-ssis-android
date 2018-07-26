package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.DisbursementDetail;


public class DisbursementDetailAdapter extends RecyclerView.Adapter<DisbursementDetailAdapter.ViewHolder> {
    private List<DisbursementDetail> disbursementDetails;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DisbursementDetailAdapter(List<DisbursementDetail> disbursementDetails) {
        this.disbursementDetails = disbursementDetails;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DisbursementDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.disbursement_detail_list_row, parent, false)
        );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull DisbursementDetailAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.itemCode.setText(disbursementDetails.get(position).get("itemCode").toString());
        viewHolder.itemName.setText(disbursementDetails.get(position).get("itemName").toString());
//        viewHolder.uom.setText(disbursementDetails.get(position).get("uom").toString());
        viewHolder.quantity.setText(disbursementDetails.get(position).get("quantity").toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return disbursementDetails != null ? disbursementDetails.size() : 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemCode;
        public TextView itemName;
        //        public TextView uom;
        public TextView quantity;

        public ViewHolder(View itemView) {
            super(itemView);
            itemCode = itemView.findViewById(R.id.detail_item_code);
            itemName = itemView.findViewById(R.id.detail_description);
//            uom = itemView.findViewById(R.id.detail_uom);
            quantity = itemView.findViewById(R.id.detail_quantity);
        }
    }
}