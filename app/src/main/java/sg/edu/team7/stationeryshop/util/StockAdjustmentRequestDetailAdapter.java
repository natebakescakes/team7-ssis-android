package sg.edu.team7.stationeryshop.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.NewStockAdjustmentActivity;
import sg.edu.team7.stationeryshop.activities.StockAdjustmentRequestDetailActivity;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequestDetail;

public class StockAdjustmentRequestDetailAdapter extends RecyclerView.Adapter<StockAdjustmentRequestDetailAdapter.ViewHolder> {
    private List<StockAdjustmentRequestDetail> stockAdjustmentDetails;
    private Activity activity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public StockAdjustmentRequestDetailAdapter(List<StockAdjustmentRequestDetail> stockAdjustmentDetails, StockAdjustmentRequestDetailActivity activity) {
        this.stockAdjustmentDetails = stockAdjustmentDetails;
        this.activity = activity;
    }

    public StockAdjustmentRequestDetailAdapter(List<StockAdjustmentRequestDetail> stockAdjustmentDetails,NewStockAdjustmentActivity activity) {
        this.stockAdjustmentDetails = stockAdjustmentDetails;
        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StockAdjustmentRequestDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.stock_adjustment_request_detail_list_row, parent, false)
        );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull StockAdjustmentRequestDetailAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        String originalQuantity = stockAdjustmentDetails.get(position).get("originalQuantity").toString();
        String afterQuantity = stockAdjustmentDetails.get(position).get("afterQuantity").toString();

        viewHolder.itemCode.setText(stockAdjustmentDetails.get(position).get("itemCode").toString());
        viewHolder.itemName.setText(stockAdjustmentDetails.get(position).get("itemName").toString());
        viewHolder.originalQuantity.setText(originalQuantity);
        viewHolder.afterQuantity.setText(afterQuantity);
        viewHolder.reason.setText(stockAdjustmentDetails.get(position).get("reason").toString());

        if (Integer.parseInt(afterQuantity) > Integer.parseInt(originalQuantity))
            viewHolder.afterQuantity.setTextColor(activity.getColor(R.color.colorPrimary));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stockAdjustmentDetails != null ? stockAdjustmentDetails.size() : 0;
    }

    // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemCode;
        public TextView itemName;
        public TextView originalQuantity;
        public TextView afterQuantity;
        public TextInputEditText reason;

        public ViewHolder(View itemView) {
            super(itemView);
            itemCode = itemView.findViewById(R.id.stock_adjustment_item_code);
            itemName = itemView.findViewById(R.id.stock_adjustment_item_name);
            originalQuantity = itemView.findViewById(R.id.original_quantity_value);
            afterQuantity = itemView.findViewById(R.id.after_quantity_value);
            reason = itemView.findViewById(R.id.detail_reason);
        }
    }

    public void update(List<StockAdjustmentRequestDetail> list) {
       // this.stockAdjustmentDetails.clear();
        //this.stockAdjustmentDetails.addAll(list);
        this.stockAdjustmentDetails = list;
        notifyDataSetChanged();
    }
}
