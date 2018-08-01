package sg.edu.team7.stationeryshop.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.MainActivity;
import sg.edu.team7.stationeryshop.activities.RequisitionDetailActivity;
import sg.edu.team7.stationeryshop.util.JSONParser;

import static android.content.Context.MODE_PRIVATE;
// ...

public class RejectRequisitionDialogFragment extends DialogFragment {

    public RejectRequisitionDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static RejectRequisitionDialogFragment newInstance(String title) {
        RejectRequisitionDialogFragment frag = new RejectRequisitionDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reject_requisition_dialog, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Dimensions
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        TextInputEditText editText = view.findViewById(R.id.dialog_head_remarks);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Rejection Remarks");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        editText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Initialize FAB
        FloatingActionButton rejectButton = view.findViewById(R.id.reject_fab);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        JSONObject requisitionId = new JSONObject();
                        try {
                            requisitionId.put("RequisitionId", RequisitionDetailActivity.requisition.get("requisitionId").toString());
                            requisitionId.put("Email", getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));
                            requisitionId.put("Remarks", editText.getText().toString());

                            String message = JSONParser.postStream(
                                    MainActivity.getContext().getString(R.string.default_hostname) + "/api/requisition/reject",
                                    requisitionId.toString()
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
                        RequisitionDetailActivity.progressDialog.setTitle("Rejecting requisition");
                        RequisitionDetailActivity.progressDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String message) {
                        RequisitionDetailActivity.progressDialog.dismiss();
                        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        new RequisitionRequestFragment.UpdateRequisition().execute();
                    }
                }.execute();
            }
        });


    }
}