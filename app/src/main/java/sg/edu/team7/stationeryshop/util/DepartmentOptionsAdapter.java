package sg.edu.team7.stationeryshop.util;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.MainActivity;
import sg.edu.team7.stationeryshop.fragments.DepartmentOptionsFragment;
import sg.edu.team7.stationeryshop.models.Delegation;
import sg.edu.team7.stationeryshop.models.DepartmentOptions;
import sg.edu.team7.stationeryshop.models.Employee;

import static android.content.Context.MODE_PRIVATE;


public class DepartmentOptionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private List<Delegation> delegations;
    private List<Employee> employees;
    private DepartmentOptions departmentOptions;
    private DepartmentOptionsFragment fragment;

    // Provide a suitable constructor (depends on the kind of dataset)
    public DepartmentOptionsAdapter(DepartmentOptions departmentOptions, DepartmentOptionsFragment fragment) {
        this.departmentOptions = departmentOptions;
        this.fragment = fragment;
        this.delegations = new ArrayList<>();
        this.employees = new ArrayList<>();
        List delegations = (List) departmentOptions.get("delegations");
        List employees = (List) departmentOptions.get("employees");
        delegations.stream().forEach(d -> this.delegations.add((Delegation) d));
        employees.stream().forEach(e -> this.employees.add((Employee) e));
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) viewHolder).representative.setText(departmentOptions.get("representative").toString());
            ((HeaderViewHolder) viewHolder).edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
                    builder.setTitle("Choose a Representative:");
                    builder.setItems(
                            employees.stream().map(rep -> rep.get("name")).collect(Collectors.toList()).toArray(new String[employees.size()]),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int position) {
                                    new AsyncTask<Void, Void, String>() {
                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            JSONObject representative = new JSONObject();

                                            try {
                                                representative.put("RepresentativeEmail", employees.stream().map(r -> r.get("email").toString()).collect(Collectors.toList()).get(position));
                                                representative.put("HeadEmail", fragment.getContext().getSharedPreferences(fragment.getContext().getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));

                                                String message = JSONParser.postStream(
                                                        MainActivity.getContext().getString(R.string.default_hostname) + "/api/departmentoptions/representative",
                                                        representative.toString()
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
                                            fragment.progressDialog.setTitle("Changing representatives...");
                                            fragment.progressDialog.show();
                                        }

                                        @Override
                                        protected void onPostExecute(String message) {
                                            fragment.progressDialog.dismiss();
                                            Toast.makeText(fragment.getContext(), message, Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    }.execute();
                                    // Change Representative
                                }
                            });

                    builder.show();
                }
            });
        } else if (viewHolder instanceof ItemViewHolder) {
            ((ItemViewHolder) viewHolder).recipient.setText(delegations.get(position - 1).get("recipient").toString());
            ((ItemViewHolder) viewHolder).startDate.setText(delegations.get(position - 1).get("startDate").toString());
            ((ItemViewHolder) viewHolder).endDate.setText(delegations.get(position - 1).get("endDate").toString());
            ((ItemViewHolder) viewHolder).status.setText(delegations.get(position - 1).get("status").toString());
        } else if (viewHolder instanceof FooterViewHolder) {
            ((FooterViewHolder) viewHolder).create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.showDelegateDialog("Delegate Manager Role", employees);
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegation_list_header, parent, false);
            return new HeaderViewHolder(layoutView);
        } else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegation_list_row, parent, false);
            return new ItemViewHolder(layoutView);
        } else if (viewType == TYPE_FOOTER) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.delegation_list_footer, parent, false);
            return new FooterViewHolder(layoutView);
        }

        throw new RuntimeException("No match for " + viewType);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return delegations != null ? delegations.size() + 2 : 0;
    }

    public void update(List<Delegation> delegations) {
        this.delegations.clear();
        this.delegations.addAll(delegations);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        if (isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == delegations.size() + 1;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView recipient;
        public TextView startDate;
        public TextView endDate;
        public TextView status;

        public ItemViewHolder(View itemView) {
            super(itemView);
            recipient = itemView.findViewById(R.id.card_name_value);
            startDate = itemView.findViewById(R.id.card_start_date_value);
            endDate = itemView.findViewById(R.id.card_end_date_value);
            status = itemView.findViewById(R.id.card_status);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public TextView representative;
        public Button edit;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            representative = itemView.findViewById(R.id.card_representative_value);
            edit = itemView.findViewById(R.id.edit_button);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public Button create;

        public FooterViewHolder(View itemView) {
            super(itemView);
            create = itemView.findViewById(R.id.create_button);
        }
    }
}