package com.melodies.bandup.SoundCloudFragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.melodies.bandup.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoundCloudPlayerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoundCloudPlayerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoundCloudPlayerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    WebView mSoundCloudPlayer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SoundCloudPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoundCloudPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoundCloudPlayerFragment newInstance(String param1, String param2) {
        SoundCloudPlayerFragment fragment = new SoundCloudPlayerFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_sound_cloud_player, container, false);
        mSoundCloudPlayer = (WebView) rootView.findViewById(R.id.webview_soundcloud_player);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void instantiatePlayer(){
        String VIDEO_URL = "https://soundcloud.com/neongoldrecords/tigertown-lonely-cities-wingtip-remix-1";

        String html = "<!DOCTYPE html><html> <head> <meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"target-densitydpi=high-dpi\" /> <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\"> <link rel=\"stylesheet\" media=\"screen and (-webkit-device-pixel-ratio:1.5)\" href=\"hdpi.css\" /></head> <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> <iframe id=\"sc-widget " +
                "\" width=\"100%\" height=\"50%\"" + // Set Appropriate Width and Height that you want for SoundCloud Player
                " src=\"" + VIDEO_URL   // Set Embedded url
                + "\" frameborder=\"no\" scrolling=\"no\"></iframe>" +
                "<script src=\"https://w.soundcloud.com/player/api.js\" type=\"text/javascript\"></script> </body> </html> ";

        mSoundCloudPlayer.setVisibility(View.VISIBLE);
        mSoundCloudPlayer.getSettings().setJavaScriptEnabled(true);
        mSoundCloudPlayer.getSettings().setLoadWithOverviewMode(true);
        mSoundCloudPlayer.getSettings().setUseWideViewPort(true);
        mSoundCloudPlayer.loadDataWithBaseURL("",html,"text/html", "UTF-8", "");
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
