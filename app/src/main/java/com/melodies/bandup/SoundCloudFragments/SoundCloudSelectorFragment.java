package com.melodies.bandup.SoundCloudFragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Request;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * Fragment allowing user to select a track from his(or her) soundcloud account
 *
 */
public class SoundCloudSelectorFragment extends Fragment implements View.OnClickListener, Dialog.OnClickListener{
    private static String TAG = "SoundCloudSelector";
    private OnFragmentInteractionListener mListener;

    private Button mSelectionButton;
    private TextView mSongName;

    JSONArray mTracksArray = new JSONArray();
    
    private int mSoundCloudID;
    private String mSoundCloudUrl;
    private String mSoundCloudSongName;

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
    public static SoundCloudSelectorFragment newInstance(int soundCloudId, String soundCloudUrl, String soundCloudSongName) {
        SoundCloudSelectorFragment fragment = new SoundCloudSelectorFragment();
        Bundle args = new Bundle();
        args.putInt("soundcloudID", soundCloudId);
        args.putString("soundcloudURL", soundCloudUrl);
        args.putString("soundcloudSongName", soundCloudSongName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSoundCloudID = getArguments().getInt("soundcloudID");
            mSoundCloudUrl = getArguments().getString("soundcloudURL");
            mSoundCloudSongName = getArguments().getString("soundcloudSongName");
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

    /**
     * populate object variables
     *
     * @param rootView
     */
    private void setupViews(View rootView) {
        mSelectionButton = (Button)   rootView.findViewById(R.id.select_song_btn);
        mSongName        = (TextView) rootView.findViewById(R.id.current_song_text);
        if (mSoundCloudUrl == null) {
            mSongName.setText("No song selected");
        } else {
            if (mSoundCloudSongName != null && mSoundCloudSongName.equals("")) {
                mSongName.setText("The selected song has no name");
            } else {
                mSongName.setText(mSoundCloudSongName);
            }

        }

        mSelectionButton.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            outState.putInt(TAG, 1);
        } catch (Exception ex) {
            ex.printStackTrace();
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

    /**
     * Called when the user wants to change currently selected sound sample
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        // all http requests must be in own thread (so we start a new one)
        new Thread(new Runnable() {
            @Override
            public void run() {
                ApiWrapper soundcloud = new ApiWrapper(
                        getResources().getString(R.string.soundCloudClient),
                        getResources().getString(R.string.soundCloudSecret), null, null);

                try {
                    HttpResponse response = soundcloud.get(Request.to(String.format("/users/%d/tracks", mSoundCloudID)));
                    // cannot get JSON array directly from response we must use reader
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    JsonReader reader = new JsonReader(rd);
                    reader.beginArray();
                    mTracksArray = new JSONArray();
                    while (reader.hasNext()){
                        mTracksArray.put(parseTracksObject(reader));
                    }
                    reader.endArray();
                    // all dialog activity must run on UI thread
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Dialog dialog = makeSelector(mTracksArray);
                            dialog.show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * create a list pop-up dialog that includes all tracks from arr
     *
     * @param arr
     * @return selectionDialog
     */
    private Dialog makeSelector(JSONArray arr) {
        List<String> titles = new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                titles.add(arr.getJSONObject(i).getString("title"));
            }
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
            dialogBuilder.setTitle("Please select a song")
                    .setItems(titles.toArray(new CharSequence[titles.size()]), this);
            return dialogBuilder.create();
        }catch (JSONException ex){
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * parse a track from JsonReader and return a new JSONObject
     * with all values we are interested in
     *
     * @param reader
     * @return TrackJsonObject
     */
    private JSONObject parseTracksObject(JsonReader reader) {
        JSONObject track = new JSONObject();
        try {
            reader.beginObject();
            while (reader.hasNext()){
                String name = reader.nextName();

                switch (name){
                    case "id":
                        track.put("id", reader.nextInt());
                        break;
                    case "permalink_url":
                        track.put("permalink_url", reader.nextString());
                        break;
                    case "title":
                        track.put("title", reader.nextString());
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return track;
    }

    /**
     * get the selected JSON object and send it to Band-up backend
     * also set currently selected text to appropriate title
     *
     * @param dialog
     * @param which
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            JSONObject selected = mTracksArray.getJSONObject(which);
            JSONObject requestJson = new JSONObject();
            requestJson.put("soundcloudurl", selected.getString("permalink_url"));
            requestJson.put("soundcloudsongname", selected.getString("title"));

            DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().sendSoundCloudUrl(requestJson,
                    new BandUpResponseListener() {
                        @Override
                        public void onBandUpResponse(Object response) {
                            // everything was succesfull we do not need to respond
                            Log.d(TAG, "Succesfully saved");
                        }
                    }, new BandUpErrorListener() {
                        @Override
                        public void onBandUpErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });

            mSongName.setText(selected.getString("title"));
            mTracksArray = new JSONArray();
        } catch (JSONException e) {
            e.printStackTrace();
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
