package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.melodies.bandup.R;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearchFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    // View objects
    private EditText     mUsername;
    private RangeSeekBar mSeekBarAges;
    private Button       mInstruments;
    private TextView     mSelectedInstruments;
    private Button       mGenres;
    private TextView     mSelectedGenres;
    private Button       mSearch;

    public UserSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSearchFragment newInstance() {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_search, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        mUsername            = (EditText) rootView.findViewById(R.id.et_search_username);
        mSeekBarAges         = (RangeSeekBar) rootView.findViewById(R.id.search_seekBarAges);
        mInstruments         = (Button) rootView.findViewById(R.id.btn_select_instruments);
        mSelectedInstruments = (TextView) rootView.findViewById(R.id.txt_select_instruments);
        mGenres              = (Button) rootView.findViewById(R.id.btn_select_genres);
        mSelectedGenres      = (TextView) rootView.findViewById(R.id.txt_select_genres);
        mSearch              = (Button) rootView.findViewById(R.id.btn_search);
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
        void onFragmentInteraction(Uri uri);
    }
}
