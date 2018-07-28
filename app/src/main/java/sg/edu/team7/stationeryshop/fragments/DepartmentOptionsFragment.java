package sg.edu.team7.stationeryshop.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.Delegation;
import sg.edu.team7.stationeryshop.models.DepartmentOptions;
import sg.edu.team7.stationeryshop.models.Employee;
import sg.edu.team7.stationeryshop.util.DepartmentOptionsAdapter;
import sg.edu.team7.stationeryshop.util.JSONParser;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DepartmentOptionsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DepartmentOptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DepartmentOptionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static List<Delegation> delegations;
    public DepartmentOptionsAdapter mAdapter;
    public ProgressDialog progressDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public DepartmentOptionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DepartmentOptionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DepartmentOptionsFragment newInstance(String param1, String param2) {
        DepartmentOptionsFragment fragment = new DepartmentOptionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (mListener != null) {
            mListener.onFragmentInteraction("Department Options");
        }
        View view = inflater.inflate(R.layout.fragment_department_options, container, false);

        // Initialize RecyclerView
        RecyclerView mRecyclerView = view.findViewById(R.id.department_options_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        new DepartmentOptionsTask(mRecyclerView).execute();

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(getContext());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void showDelegateDialog(String title, List<Employee> employees) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DelegateDialogFragment delegateDialogFragment = DelegateDialogFragment.newInstance(title);
        delegateDialogFragment.setCallingFragment(this);
        delegateDialogFragment.setEmployees(employees);
        delegateDialogFragment.show(fm, "fragment_edit_name");
    }

    public class DepartmentOptionsTask extends AsyncTask<Void, Void, DepartmentOptions> {

        private RecyclerView recyclerView;

        public DepartmentOptionsTask(RecyclerView recyclerView) {
            this.recyclerView = recyclerView;
        }

        @Override
        protected DepartmentOptions doInBackground(Void... voids) {
            JSONObject email = new JSONObject();
            try {
                email.put("Email", getContext().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE).getString("email", ""));
                String response = JSONParser.postStream(getString(R.string.default_hostname) + "/api/departmentoptions", email.toString());
                JSONObject departmentOptionsJson = new JSONObject(response);

                List<Delegation> delegations = new ArrayList<>();
                List<Employee> employees = new ArrayList<>();

                for (int i = 0; i < departmentOptionsJson.getJSONArray("Delegations").length(); i++) {
                    JSONObject delegationJson = departmentOptionsJson.getJSONArray("Delegations").getJSONObject(i);

                    Delegation delegation = new Delegation(
                            delegationJson.getInt("DelegationId"),
                            delegationJson.getString("Recipient"),
                            delegationJson.getString("StartDate"),
                            delegationJson.getString("EndDate"),
                            delegationJson.getString("Status"));

                    delegations.add(delegation);
                }

                for (int i = 0; i < departmentOptionsJson.getJSONArray("Employees").length(); i++) {
                    JSONObject employeeJson = departmentOptionsJson.getJSONArray("Employees").getJSONObject(i);

                    Employee employee = new Employee(
                            employeeJson.getString("Name"),
                            employeeJson.getString("Email"));

                    employees.add(employee);
                }

                DepartmentOptions departmentOptions = new DepartmentOptions(
                        departmentOptionsJson.getString("Department"),
                        departmentOptionsJson.getString("Representative"),
                        delegations,
                        employees);

                return departmentOptions;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(DepartmentOptions departmentOptions) {
            List<Delegation> delegations = new ArrayList<>();
            List<Employee> employees = new ArrayList<>();
            ((List) departmentOptions.get("delegations")).stream().forEach(d -> delegations.add((Delegation) d));
            ((List) departmentOptions.get("employees")).stream().forEach(e -> employees.add((Employee) e));

            mAdapter = new DepartmentOptionsAdapter(
                    new DepartmentOptions(
                            departmentOptions.get("department").toString(),
                            departmentOptions.get("representative").toString(),
                            delegations,
                            employees),
                    DepartmentOptionsFragment.this);
            recyclerView.setAdapter(mAdapter);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String title);
    }
}
