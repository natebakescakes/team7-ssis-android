package sg.edu.team7.stationeryshop.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.activities.DisbursementDetailActivity;
import sg.edu.team7.stationeryshop.models.Disbursement;
import sg.edu.team7.stationeryshop.util.DisbursementAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DisbursementFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DisbursementFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisbursementFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static DisbursementAdapter mAdapter;
    private static List<Disbursement> disbursements;
    private static SwipeRefreshLayout mSwipeRefreshLayout;
    private static ProgressDialog progressDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public DisbursementFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisbursementFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisbursementFragment newInstance(String param1, String param2) {
        DisbursementFragment fragment = new DisbursementFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public List<Disbursement> getDisbursements() {
        return disbursements;
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

        if (mListener != null) {
            mListener.onFragmentInteraction("Disbursements");
        }

        View view = inflater.inflate(R.layout.fragment_disbursement, container, false);

        // Initialize Button
        Button allButton = view.findViewById(R.id.button_all);
        Button activeButton = view.findViewById(R.id.button_active);
        allButton.setEnabled(false);
        activeButton.setEnabled(true);

        // Initialize Disbursements
        disbursements = new ArrayList<>();

        // Initialize RecyclerView
        RecyclerView mRecyclerView = view.findViewById(R.id.disbursement_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new DisbursementAdapter(new ArrayList<>(), new DisbursementAdapter.OnItemClickListener() {
            // TODO: Incoming animation should be from right
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), DisbursementDetailActivity.class);
                intent.putExtra("disbursement", disbursements.get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        new UpdateDisbursements().execute();

        // Set SwipeLayoutRefresh
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateDisbursements().execute();
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        // Set Button onClickListener
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(false);
                activeButton.setEnabled(true);
                mAdapter.update(disbursements);
            }
        });

        activeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(true);
                activeButton.setEnabled(false);
                mAdapter.update(disbursements.stream()
                        .filter(d -> d.get("status").toString().equals("Ready For Collection"))
                        .collect(Collectors.toList()));
            }
        });

        // Initialize SearchView
        SearchView searchView = view.findViewById(R.id.search_view);
        searchView.setOnClickListener(v -> searchView.setIconified(false));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            // TODO: Will reset view not considering active button when changed to empty query
            @Override
            public boolean onQueryTextChange(String s) {
                if (activeButton.isEnabled()) // ALL BUTTON IS SELECTED
                    mAdapter.update(disbursements.stream()
                            .filter(d -> d.get("disbursementId").toString().toLowerCase().contains(s.toLowerCase()) ||
                                    d.get("department").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));
                else
                    mAdapter.update(disbursements.stream()
                            .filter(d -> d.get("status").toString().equals("Ready For Collection"))
                            .filter(d -> d.get("disbursementId").toString().toLowerCase().contains(s.toLowerCase()) ||
                                    d.get("department").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));

                return true;
            }
        });

        // Initialize Progress Dialog
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        // Inflate the layout for this fragment
        return view;
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
        void onFragmentInteraction(String title);
    }

    public static class UpdateDisbursements extends AsyncTask<Void, Void, List<Disbursement>> {
        @Override
        protected List<Disbursement> doInBackground(Void... voids) {
            try {
                return Disbursement.findAllDisbursements();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DisbursementFragment.progressDialog.setTitle("Loading Disbursements...");
            DisbursementFragment.progressDialog.show();
        }

        @Override
        protected void onPostExecute(List<Disbursement> disbursements) {
            DisbursementFragment.progressDialog.hide();
            if (disbursements != null) {
                DisbursementFragment.disbursements = disbursements;
                DisbursementFragment.mAdapter.update(disbursements);
            }
        }
    }
}
