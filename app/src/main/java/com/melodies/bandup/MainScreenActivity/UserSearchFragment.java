package com.melodies.bandup.MainScreenActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserSearchFragment extends Fragment {
    private static final String TAG = "UserSearchFragment";
    private OnFragmentInteractionListener mListener;

    // View objects
    private EditText     mUsername;
    private RangeSeekBar<Number> mSeekBarAges;
    private Button       mInstruments;
    private TextView     mSelectedInstruments;
    private Button       mGenres;
    private TextView     mSelectedGenres;
    private Button       mSearch;

    // the id's of all selected genres (data that will be sent to search query)
    private ArrayList<CharSequence> mSelectedGenreIdList = new ArrayList<>();
    // keeps track of names that are selected  (purely for UI purposes)
    private ArrayList<CharSequence> mSelectedGenreNames = new ArrayList<>();
    // specifies if an index in the genres list obtained from backend
    private boolean[] mIsSelectedGenreIndex = new boolean[20];


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

        View rootView = inflater.inflate(R.layout.fragment_user_search, container, false);
        findViews(rootView);
        return rootView;
    }

    /**
     * find all views to be manipulated and set them to private variables
     *
     * @param rootView
     */
    private void findViews(View rootView) {
        mUsername            = (EditText) rootView.findViewById(R.id.et_search_username);
        mSeekBarAges         = (RangeSeekBar<Number>) rootView.findViewById(R.id.search_seekBarAges);
        mInstruments         = (Button) rootView.findViewById(R.id.btn_select_instruments);
        mSelectedInstruments = (TextView) rootView.findViewById(R.id.txt_select_instruments);
        mGenres              = (Button) rootView.findViewById(R.id.btn_select_genres);
        mSelectedGenres      = (TextView) rootView.findViewById(R.id.txt_select_genres);
        mSearch              = (Button) rootView.findViewById(R.id.btn_search);
    }

    /**
     * display a dialog to show genres and allow user to select multiple
     *
     * @param v View which is calling this function
     */

    public void onShowGenres(View v){
        Log.d(TAG, "Show genres pushed");
        DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().getGenres(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                createGenresDialog((JSONArray) response);
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                Log.d(TAG, error.getMessage());
                Toast.makeText(getContext(),
                        "oops, hit an unexpected error while fetching instruments",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * create and display selection dialog made from the genres listed in the response object
     * @param response an array of all genres
     */
    private void createGenresDialog(JSONArray response) {
        try {
            CharSequence[] itemNames = new CharSequence[response.length()];
            ArrayList<CharSequence> itemIds = new ArrayList<>();
            for (int i = 0; i < response.length(); i++){
                JSONObject curr = response.getJSONObject(i);
                itemNames[i] = curr.getString("name");
                itemIds.add(curr.getString("_id"));
            }
            Log.d(TAG, "All items added to list");

            Dialog myDialog = createGenresSelectionDialog(itemNames, itemIds);
            myDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * use dialog builder to create checkbox dialog from itemNames and select itemIds
     *
     * @param itemNames list of all genre names to display
     * @param itemIds list of all genre id's to display (id index corresponds to name index)
     * @return dialog
     */
    private Dialog createGenresSelectionDialog(final CharSequence[] itemNames, final ArrayList<CharSequence> itemIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final ArrayList<CharSequence> backup = (ArrayList<CharSequence>) mSelectedGenreIdList.clone();
        //mSelectedGenreIdList.clear();
        builder.setTitle("Select genres")
                .setMultiChoiceItems(itemNames, mIsSelectedGenreIndex, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // when item is selected or de-selected
                        if (isChecked){
                            // add item id to selected
                            mSelectedGenreIdList.add(itemIds.get(which));
                            mSelectedGenreNames.add(itemNames[which]);
                            mIsSelectedGenreIndex[which] = true;
                        }else if (mSelectedGenreIdList.contains(itemIds.get(which))){
                            // remove item from list
                            mSelectedGenreIdList.remove(itemIds.get(which));
                            mSelectedGenreNames.remove(itemNames[which]);
                            mIsSelectedGenreIndex[which] = false;
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // set text on genre list element
                        mSelectedGenres.setText("");
                        for (int i = 0; i < mSelectedGenreNames.size(); i++){
                            mSelectedGenres.append(mSelectedGenreNames.get(i));
                            if (!(i == mSelectedGenreNames.size() - 1)){
                                mSelectedGenres.append(", ");
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // restore selected genres to previous state
                        mSelectedGenreIdList = backup;
                    }
                });

        return builder.create();
    }

    /**
     * take all parameters from form and send search request
     * @param view
     */
    public void onClickSearch(View view){
        Log.d(TAG, "Search initialized");
        JSONObject queryObject = makeQueryJson();
        makeQuery(queryObject);
    }

    /**
     * make and send request to server and respond to results
     *
     * @param queryObject JSON query for server
     */
    private void makeQuery(JSONObject queryObject) {
    }

    /**
     * take all information from form and if not in default value state insert into query
     *
     * @return JSONObject that represents our query
     */
    private JSONObject makeQueryJson() {
        // get values
        String username = mUsername.getText().toString();
        int minAge = mSeekBarAges.getSelectedMinValue().intValue();
        int maxAge = mSeekBarAges.getSelectedMaxValue().intValue();

        // construct JSON
        JSONObject query = new JSONObject();

        return query;
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
