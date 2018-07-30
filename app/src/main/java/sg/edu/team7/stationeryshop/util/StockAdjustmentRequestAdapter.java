package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequest;

public class StockAdjustmentRequestAdapter extends RecyclerView.Adapter<StockAdjustmentRequestAdapter.ViewHolder> {
    private List<StockAdjustmentRequest> stockAdjustmentRequests;
    private StockAdjustmentRequestAdapter.OnItemClickListener onItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public StockAdjustmentRequestAdapter(List<StockAdjustmentRequest> stockAdjustmentRequests, StockAdjustmentRequestAdapter.OnItemClickListener onItemClickListener) {
        this.stockAdjustmentRequests = stockAdjustmentRequests;
        this.onItemClickListener = onItemClickListener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull StockAdjustmentRequestAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.stockAdjustmentId.setText(stockAdjustmentRequests.get(position).get("stockAdjustmentId").toString());
        viewHolder.status.setText(stockAdjustmentRequests.get(position).get("status").toString());
        viewHolder.requestedDate.setText(stockAdjustmentRequests.get(position).get("requestedDate").toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public StockAdjustmentRequestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new StockAdjustmentRequestAdapter.ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.stock_adjustment_request_list_row, parent, false)
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return stockAdjustmentRequests != null ? stockAdjustmentRequests.size() : 0;
    }

    public void update(List<StockAdjustmentRequest> stockAdjustmentRequests) {
        this.stockAdjustmentRequests.clear();
        this.stockAdjustmentRequests.addAll(stockAdjustmentRequests);
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
        public TextView stockAdjustmentId;
        public TextView status;
        public TextView requestedDate;

        public ViewHolder(View itemView) {
            super(itemView);
            stockAdjustmentId = itemView.findViewById(R.id.stock_adjustment_id);
            status = itemView.findViewById(R.id.stock_adjustment_status);
            requestedDate = itemView.findViewById(R.id.stock_adjustment_date);
        }
    }
}
