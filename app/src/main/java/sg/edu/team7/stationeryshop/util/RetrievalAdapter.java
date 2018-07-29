package sg.edu.team7.stationeryshop.util;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.Retrieval;

public class RetrievalAdapter extends RecyclerView.Adapter<RetrievalAdapter.ViewHolder> {
    private List<Retrieval> retrievals;
    private RetrievalAdapter.OnItemClickListener onItemClickListener;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RetrievalAdapter(List<Retrieval> retrievals, RetrievalAdapter.OnItemClickListener onItemClickListener) {
        this.retrievals = retrievals;
        this.onItemClickListener = onItemClickListener;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RetrievalAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.retrievalId.setText(retrievals.get(position).get("retrievalId").toString());
        viewHolder.status.setText(retrievals.get(position).get("status").toString());
        viewHolder.createdDate.setText(retrievals.get(position).get("createdDate").toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(view, position);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RetrievalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RetrievalAdapter.ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.retrieval_list_row, parent, false)
        );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return retrievals != null ? retrievals.size() : 0;
    }

    public void update(List<Retrieval> retrievals) {
        this.retrievals.clear();
        this.retrievals.addAll(retrievals);
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
        public TextView retrievalId;
        public TextView status;
        public TextView createdDate;

        public ViewHolder(View itemView) {
            super(itemView);
            retrievalId = itemView.findViewById(R.id.retrieval_id);
            status = itemView.findViewById(R.id.retrieval_status);
            createdDate = itemView.findViewById(R.id.created_date);
        }
    }
}
