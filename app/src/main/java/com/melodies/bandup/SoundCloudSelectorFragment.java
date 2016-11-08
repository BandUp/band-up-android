package com.melodies.bandup;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.melodies.bandup.helper_classes.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoundCloudSelectorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoundCloudSelectorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundCloudSelectorFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private View mRootView;

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

    private void createSoundCloudWidget() {
        if (soundCloudId == 0){
            // needs to add soudcloud link
            // load appropriate image
            ImageView imageView = new ImageView(getContext());
            imageView.setBackground(getResources().getDrawable(R.drawable.soundcloud_big));
            //imageView.setMaxHeight(100);
            ((FrameLayout)mRootView).addView(imageView);
        }else if (soundCloudURL == null){
            // has not chosen a sound byte
            // start from soundbyte selection
        }else{
            // we have both values we can create widget
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_soundcloudselector, container, false);
        mRootView.setOnClickListener(this);
        createSoundCloudWidget();
        return mRootView;
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
        if (soundCloudId == 0){
            System.out.println("loggin in with soundcloud");
        }else if (soundCloudURL == null){
            // has not chosen a sound byte
            // start from soundbyte selection
        }else{
            // we have both values we can create widget
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
        void onFragmentInteraction(Uri uri);
    }
}
