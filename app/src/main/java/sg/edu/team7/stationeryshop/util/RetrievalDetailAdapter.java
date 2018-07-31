package sg.edu.team7.stationeryshop.util;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.RetrievalDetailActivity;
import sg.edu.team7.stationeryshop.activities.RetrievalDetailByDeptActivity;
import sg.edu.team7.stationeryshop.models.RetrievalDetail;
import sg.edu.team7.stationeryshop.models.RetrievalDetailByDept;

import static android.content.Context.MODE_PRIVATE;

public class RetrievalDetailAdapter extends RecyclerView.Adapter<RetrievalDetailAdapter.ViewHolder> {
    private final RetrievalDetailActivity activity;

    private final List<RetrievalDetail> retrievalDetails;
    private List<RetrievalDetailByDept> retrievalDetailByDepts;
    private String retrievalId;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RetrievalDetailAdapter(String retrievalId, List<RetrievalDetailByDept> retrievalDetailByDepts, RetrievalDetailActivity activity) {
        this.retrievalDetailByDepts = retrievalDetailByDepts;
        this.retrievalId = retrievalId;

        // Consolidate By ItemCode
        retrievalDetails = new ArrayList<>();
        retrievalDetailByDepts.forEach(retrievalDetailByDept -> {
            // if retrievalDetails contains retrievalDetail that has same itemCode key
            // add quantity to key
            // else
            // create new retrievalDetail with item key
            String itemCode = retrievalDetailByDept.get("itemCode").toString();
            int planQuantity = Integer.parseInt(retrievalDetailByDept.get("planQuantity").toString());
            int actualQuanttiy = Integer.parseInt(retrievalDetailByDept.get("actualQuantity").toString());

            // if retrievalDetails contains retrievalDetail that has same itemCode key
            if (retrievalDetails.stream().filter(rd -> rd.get("itemCode").toString().equals(itemCode)).count() > 0) {
                // Get RetrievalDetail that has same itemCode
                RetrievalDetail retrievalDetail = retrievalDetails.stream().filter(rd -> rd.get("itemCode").toString().equals(itemCode)).findFirst().get();

                // Get current RetrievalDetail quantity
                int currentPlanQuantity = Integer.parseInt(retrievalDetail.get("planQuantity").toString());
                int currentActualQuantity = Integer.parseInt(retrievalDetail.get("actualQuantity").toString());
                // Add RetrievalDetailByDept quantity
                retrievalDetail.put("planQuantity", currentPlanQuantity + planQuantity);
                retrievalDetail.put("actualQuantity", currentActualQuantity + actualQuanttiy);
            } else {
                retrievalDetails.add(new RetrievalDetail(
                        itemCode,
                        retrievalDetailByDept.get("itemName").toString(),
                        retrievalDetailByDept.get("bin").toString(),
                        retrievalDetailByDept.get("uom").toString(),
                        retrievalDetailByDept.get("status").toString(),
                        planQuantity,
                        actualQuanttiy));
            }
        });

        this.activity = activity;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RetrievalDetailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RetrievalDetailAdapter.ViewHolder(LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.retrieval_detail_list_row, parent, false)
        );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RetrievalDetailAdapter.ViewHolder viewHolder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.itemCode.setText(retrievalDetails.get(position).get("itemCode").toString());
        viewHolder.itemName.setText(retrievalDetails.get(position).get("itemName").toString());
        viewHolder.uom.setText(retrievalDetails.get(position).get("uom").toString());
        viewHolder.bin.setText(retrievalDetails.get(position).get("bin").toString());
        viewHolder.quantity.setText(retrievalDetails.get(position).get("actualQuantity").toString());

        if (!retrievalDetails.get(position).get("planQuantity").equals(retrievalDetails.get(position).get("actualQuantity"))) {
            viewHolder.quantity.setTextColor(activity.getColor(R.color.colorAccent));
            viewHolder.quantity.setTypeface(null, Typeface.BOLD);
        }

        if (retrievalDetails.get(position).get("status").equals("Picked")) {
            viewHolder.retrieveButton.setEnabled(false);
            viewHolder.itemName.setPaintFlags(viewHolder.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.itemCode.setPaintFlags(viewHolder.itemCode.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.uom.setPaintFlags(viewHolder.uom.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.bin.setPaintFlags(viewHolder.bin.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.quantity.setPaintFlags(viewHolder.quantity.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        // Bind Info Button
        viewHolder.infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, RetrievalDetailByDeptActivity.class);

                intent.putExtra("retrievalId", retrievalId);
                intent.putExtra("itemCode", retrievalDetails.get(position).get("itemCode").toString());
                activity.startActivity(intent);
            }
        });

        viewHolder.retrieveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject retrieval = new JSONObject();
                        try {
                            retrieval.put("RetrievalId", RetrievalDetailAdapter.this.retrievalId);
                            retrieval.put("Email", activity.getSharedPreferences(activity.getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));
                            retrieval.put("ItemCode", retrievalDetails.get(position).get("itemCode").toString());


                            String message = JSONParser.postStream(
                                    RetrievalDetailAdapter.this.activity.getString(R.string.default_hostname) + "/api/retrieval/retrieveitem",
                                    retrieval.toString()
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
                        RetrievalDetailAdapter.this.activity.getProgressDialog().setTitle("Retrieving item");
                        RetrievalDetailAdapter.this.activity.getProgressDialog().show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        super.onPostExecute(message);
                        RetrievalDetailAdapter.this.activity.getProgressDialog().dismiss();
                        Toast.makeText(RetrievalDetailAdapter.this.activity, message, Toast.LENGTH_SHORT).show();
                    }
                }.execute();

            }
        });
    }

    public void update(List<RetrievalDetailByDept> retrievalDetailByDepts) {
        this.retrievalDetails.clear();
        retrievalDetailByDepts.forEach(retrievalDetailByDept -> {
            // if retrievalDetails contains retrievalDetail that has same itemCode key
            // add quantity to key
            // else
            // create new retrievalDetail with item key
            String itemCode = retrievalDetailByDept.get("itemCode").toString();
            int planQuantity = Integer.parseInt(retrievalDetailByDept.get("planQuantity").toString());
            int actualQuanttiy = Integer.parseInt(retrievalDetailByDept.get("actualQuantity").toString());

            // if retrievalDetails contains retrievalDetail that has same itemCode key
            if (this.retrievalDetails.stream().filter(rd -> rd.get("itemCode").toString().equals(itemCode)).count() > 0) {
                // Get RetrievalDetail that has same itemCode
                RetrievalDetail retrievalDetail = retrievalDetails.stream().filter(rd -> rd.get("itemCode").toString().equals(itemCode)).findFirst().get();

                // Get current RetrievalDetail quantity
                int currentPlanQuantity = Integer.parseInt(retrievalDetail.get("planQuantity").toString());
                int currentActualQuantity = Integer.parseInt(retrievalDetail.get("actualQuantity").toString());
                // Add RetrievalDetailByDept quantity
                retrievalDetail.put("planQuantity", currentPlanQuantity + planQuantity);
                retrievalDetail.put("actualQuantity", currentActualQuantity + actualQuanttiy);
            } else {
                this.retrievalDetails.add(new RetrievalDetail(
                        itemCode,
                        retrievalDetailByDept.get("itemName").toString(),
                        retrievalDetailByDept.get("bin").toString(),
                        retrievalDetailByDept.get("uom").toString(),
                        retrievalDetailByDept.get("status").toString(),
                        planQuantity,
                        actualQuanttiy));
            }
        });
        notifyDataSetChanged();
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return retrievalDetails != null ? retrievalDetails.size() : 0;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView itemCode;
        public TextView itemName;
        public TextView uom;
        public TextView bin;
        public TextView quantity;
        public ImageButton retrieveButton;
        public ImageButton infoButton;


        public ViewHolder(View itemView) {
            super(itemView);
            itemCode = itemView.findViewById(R.id.item_code);
            itemName = itemView.findViewById(R.id.item_name);
            uom = itemView.findViewById(R.id.uom);
            bin = itemView.findViewById(R.id.bin_label);
            quantity = itemView.findViewById(R.id.plan_quantity);
            retrieveButton = itemView.findViewById(R.id.retrieve_button);
            infoButton = itemView.findViewById(R.id.info_button);
        }
    }
}