package sg.edu.team7.stationeryshop.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import sg.edu.team7.stationeryshop.activities.RetrievalDetailActivity;
import sg.edu.team7.stationeryshop.models.Retrieval;
import sg.edu.team7.stationeryshop.util.RetrievalAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StationeryRetrievalFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationeryRetrievalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationeryRetrievalFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private static List<Retrieval> retrievals;
    private static RetrievalAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public StationeryRetrievalFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationeryRetrievalFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StationeryRetrievalFragment newInstance(String param1, String param2) {
        StationeryRetrievalFragment fragment = new StationeryRetrievalFragment();
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

        if (mListener != null) {
            mListener.onFragmentInteraction("Stationery Retrievals");
        }

        View view = inflater.inflate(R.layout.fragment_stationery_retrieval, container, false);

        // Initialize Button
        Button allButton = view.findViewById(R.id.button_all);
        Button pendingButton = view.findViewById(R.id.button_pending);
        allButton.setEnabled(false);
        pendingButton.setEnabled(true);

        // Initialize Requisition;
        retrievals = new ArrayList<>();

        // Initialize RecyclerView
        RecyclerView mRecyclerView = view.findViewById(R.id.retrieval_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RetrievalAdapter(new ArrayList<>(), new RetrievalAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), RetrievalDetailActivity.class);
                intent.putExtra("retrievalId", retrievals.get(position).get("retrievalId").toString());
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        new UpdateRetrievals().execute();

        // Set Button onClickListener
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(false);
                pendingButton.setEnabled(true);
                mAdapter.update(retrievals);
            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(true);
                pendingButton.setEnabled(false);
                mAdapter.update(retrievals.stream()
                        .filter(r -> r.get("status").toString().equals("Pending Retrieval"))
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
                if (pendingButton.isEnabled()) // ALL FILTER IS SELECTED
                    mAdapter.update(retrievals.stream()
                            .filter(r -> r.get("retrievalId").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));
                else
                    mAdapter.update(retrievals.stream()
                            .filter(r -> r.get("status").toString().equals("Pending Retrieval"))
                            .filter(r -> r.get("retrievalId").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));

                return true;
            }
        });

        // Set SwipeLayoutRefresh
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateRetrievals().execute();
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        // Inflate the layout for this fragment
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

    public static class UpdateRetrievals extends AsyncTask<Void, Void, List<Retrieval>> {
        @Override
        protected void onPostExecute(List<Retrieval> retrievals) {
            if (retrievals != null) {
                StationeryRetrievalFragment.retrievals = retrievals;
                StationeryRetrievalFragment.mAdapter.update(retrievals);
            }
        }

        @Override
        protected List<Retrieval> doInBackground(Void... voids) {
            try {
                return Retrieval.findAllRetrievals();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
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
        void onFragmentInteraction(String title);
    }
}
