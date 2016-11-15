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
    private static final String ARG_URL = "ARG_URL";

    private WebView mSoundCloudPlayer;
    private String url;

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public SoundCloudPlayerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SoundCloudPlayerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoundCloudPlayerFragment newInstance(String soundcloudURL) {
        SoundCloudPlayerFragment fragment = new SoundCloudPlayerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_URL, soundcloudURL);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(ARG_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_sound_cloud_player, container, false);
        mSoundCloudPlayer = (WebView) rootView.findViewById(R.id.webview_soundcloud_player);
        instantiatePlayer();
        return rootView;
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
        String html = getString(R.string.no_soundcloud_html);
        if (!url.equals("")) {
            html = String.format(getString(R.string.soundcloud_html), url);
        }

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
