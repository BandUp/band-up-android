package com.melodies.bandup.SoundCloudFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.melodies.bandup.R;


/**
 * Fragment allowing user to select a track from his(or her) soundcloud account
 *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoundCloudSelectorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoundCloudSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundCloudSelectorFragment extends Fragment implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;

    private Button selectionButton;
    private TextView songNameText;

    private int soundCloudId;
    private String soundCloudURL;

    public SoundCloudSelectorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SoundCloudSelectorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoundCloudSelectorFragment newInstance(int soundCloudId, String soundCloudUrl) {
        SoundCloudSelectorFragment fragment = new SoundCloudSelectorFragment();
        Bundle args = new Bundle();
        args.putInt("soundcloudID", soundCloudId);
        args.putString("soundcloudURL", soundCloudUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            soundCloudId = getArguments().getInt("soundcloudID");
            soundCloudURL = getArguments().getString("soundcloudURL");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_soundcloudselector, container, false);

        setupViews(rootView);

        return rootView;
    }

    private void setupViews(View rootView) {
        selectionButton = (Button) rootView.findViewById(R.id.select_song_btn);
        songNameText    = (TextView) rootView.findViewById(R.id.current_song_text);
        if (soundCloudURL == null){
            songNameText.setText("No song selected");
        }else {
            songNameText.setText(soundCloudURL);
        }

        selectionButton.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {

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
