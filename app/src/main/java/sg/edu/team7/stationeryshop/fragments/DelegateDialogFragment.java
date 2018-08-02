package sg.edu.team7.stationeryshop.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.savvi.rangedatepicker.CalendarPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.MainActivity;
import sg.edu.team7.stationeryshop.models.Employee;
import sg.edu.team7.stationeryshop.util.JSONParser;

import static android.content.Context.MODE_PRIVATE;
// ...

public class DelegateDialogFragment extends DialogFragment {

    private List<Employee> employees;
    private DepartmentOptionsFragment callingFragment;

    public DelegateDialogFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static DelegateDialogFragment newInstance(String title) {
        DelegateDialogFragment frag = new DelegateDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_delegate_dialog, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Dimensions
        int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_delegate_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize Spinner
        Spinner spinner = view.findViewById(R.id.employee_spinner);
        List<String> spinnerData = new ArrayList<>();
        spinnerData.add("Please select an employee to delegate");
        employees.stream().forEach(e -> spinnerData.add(e.get("name").toString()));

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerData);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        // Initialize Calendar Picker
        CalendarPickerView calendar = view.findViewById(R.id.delegate_datepicker);

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DATE, 365);

        calendar.init(Calendar.getInstance().getTime(), nextYear.getTime()) //
                .inMode(CalendarPickerView.SelectionMode.RANGE);

        // Initialize FAB
        FloatingActionButton confirmButton = view.findViewById(R.id.confirm_fab);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Delegate Manager Role");
                builder.setMessage("Are you sure you want to Delegate Manager Role");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        new AsyncTask<Void, Void, String>() {
                            @Override
                            protected String doInBackground(Void... voids) {
                                JSONObject delegation = new JSONObject();

                                try {
                                    delegation.put("RecipientEmail", employees.get(spinner.getSelectedItemPosition() - 1).get("email"));
                                    delegation.put("HeadEmail", getActivity().getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE).getString("email", ""));
                                    delegation.put("StartDate", calendar.getSelectedDates().get(0).toString());
                                    delegation.put("EndDate", calendar.getSelectedDates().get(calendar.getSelectedDates().size() - 1).toString());

                                    String message = JSONParser.postStream(
                                            MainActivity.getContext().getString(R.string.default_hostname) + "/api/departmentoptions/delegate",
                                            delegation.toString()
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
                                callingFragment.progressDialog.setTitle("Delegating manager role...");
                                callingFragment.progressDialog.show();
                            }

                            @Override
                            protected void onPostExecute(String message) {
                                callingFragment.progressDialog.dismiss();
                                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                                getDialog().dismiss();
                            }
                        }.execute();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public void setCallingFragment(DepartmentOptionsFragment callingFragment) {
        this.callingFragment = callingFragment;
    }
}