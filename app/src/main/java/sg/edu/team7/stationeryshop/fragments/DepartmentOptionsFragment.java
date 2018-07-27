package sg.edu.team7.stationeryshop.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sg.edu.team7.stationeryshop.R;
import sg.edu.team7.stationeryshop.models.Delegation;
import sg.edu.team7.stationeryshop.models.DepartmentOptions;
import sg.edu.team7.stationeryshop.util.DepartmentOptionsAdapter;

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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public static DepartmentOptionsAdapter mAdapter;

    private static List<Delegation> delegations;

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

        // SAMPLE DELEGATIONS
        List<Delegation> sampleDelegations = new ArrayList<>();
        sampleDelegations.add(new Delegation(1, "Mr. Nathan Khoo", "Wednesday, 1 August 2018", "Friday, 3 August 2018", "Enabled"));
        sampleDelegations.add(new Delegation(2, "Mr. Kathan Nhoo", "Wednesday, 1 August 2018", "Monday, 6 August 2018", "Enabled"));
        // SAMPLE DELEGATIONS

        mAdapter = new DepartmentOptionsAdapter(new DepartmentOptions("English Department", "Mr. Nathan Khoo", sampleDelegations));
//        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//                int totalWidth = parent.getWidth();
//                int cardWidth = getResources().getDimensionPixelOffset(R.dimen.small_card_width);
//                int sidePadding = (totalWidth - cardWidth) / 2;
//                sidePadding = Math.max(0, sidePadding);
//                outRect.set(sidePadding, 0, sidePadding, 0);
//            }
//        });
        mRecyclerView.setAdapter(mAdapter);

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
