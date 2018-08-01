package sg.edu.team7.stationeryshop.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import sg.edu.team7.stationeryshop.activities.AddStockAdjustmentDetailActivity;
import sg.edu.team7.stationeryshop.activities.NewStockAdjustmentActivity;
import sg.edu.team7.stationeryshop.activities.StockAdjustmentRequestDetailActivity;
import sg.edu.team7.stationeryshop.models.StockAdjustmentRequest;
import sg.edu.team7.stationeryshop.util.StockAdjustmentRequestAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StockAdjustmentRequestsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StockAdjustmentRequestsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StockAdjustmentRequestsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static StockAdjustmentRequestAdapter mAdapter;
    private static List<StockAdjustmentRequest> stockAdjustmentRequests;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public StockAdjustmentRequestsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StockAdjustmentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StockAdjustmentRequestsFragment newInstance(String param1, String param2) {
        StockAdjustmentRequestsFragment fragment = new StockAdjustmentRequestsFragment();
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
            mListener.onFragmentInteraction("Stock Adjustment Requests");
        }

        View view = inflater.inflate(R.layout.fragment_stock_adjustment_requests, container, false);

        // Initialize Button
        Button allButton = view.findViewById(R.id.button_all);
        Button pendingButton = view.findViewById(R.id.button_pending);
        allButton.setEnabled(false);
        pendingButton.setEnabled(true);
        FloatingActionButton fab = view.findViewById(R.id.new_sa);

        // Initialize Requisition;
        stockAdjustmentRequests = new ArrayList<>();

        // Initialize RecyclerView
        RecyclerView mRecyclerView = view.findViewById(R.id.stock_adjustment_requests_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new StockAdjustmentRequestAdapter(new ArrayList<>(), new StockAdjustmentRequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), StockAdjustmentRequestDetailActivity.class);
                intent.putExtra("stockAdjustment", stockAdjustmentRequests.get(position));
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        new UpdateStockAdjustment().execute();

        // Set Button onClickListener
        allButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(false);
                pendingButton.setEnabled(true);
                mAdapter.update(stockAdjustmentRequests);
            }
        });

        pendingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allButton.setEnabled(true);
                pendingButton.setEnabled(false);
                mAdapter.update(stockAdjustmentRequests.stream()
                        .filter(r -> r.get("status").toString().equals("Pending Approval"))
                        .collect(Collectors.toList()));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewStockAdjustmentActivity.class);
                startActivity(intent);

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
                    mAdapter.update(stockAdjustmentRequests.stream()
                            .filter(r -> r.get("stockAdjustmentId").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));
                else
                    mAdapter.update(stockAdjustmentRequests.stream()
                            .filter(r -> r.get("status").toString().equals("Pending Approval"))
                            .filter(r -> r.get("stockAdjustmentId").toString().toLowerCase().contains(s.toLowerCase()))
                            .collect(Collectors.toList()));

                return true;
            }
        });

        // Set SwipeLayoutRefresh
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new UpdateStockAdjustment().execute();
                if (mSwipeRefreshLayout.isRefreshing())
                    mSwipeRefreshLayout.setRefreshing(false);
            }
        });


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
    public void onResume() {
        super.onResume();
        new UpdateStockAdjustment().execute();

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

    public static class UpdateStockAdjustment extends AsyncTask<Void, Void, List<StockAdjustmentRequest>> {
        @Override
        protected void onPostExecute(List<StockAdjustmentRequest> stockAdjustmentRequests) {
            if (stockAdjustmentRequests != null) {
                StockAdjustmentRequestsFragment.stockAdjustmentRequests = stockAdjustmentRequests;
                StockAdjustmentRequestsFragment.mAdapter.update(stockAdjustmentRequests);
            }
        }

        @Override
        protected List<StockAdjustmentRequest> doInBackground(Void... voids) {
            try {
                return StockAdjustmentRequest.findAllStockAdjustments();
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
