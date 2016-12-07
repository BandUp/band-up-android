package com.melodies.bandup.MainScreenActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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
    private EditText             mUsername;
    private RangeSeekBar<Number> mSeekBarAges;
    private TextView             mSelectedInstruments;
    private TextView             mSelectedGenres;
    private Button               mInstruments;
    private Button               mGenres;
    private Button               mSearch;

    // The id's of all selected genres (data that will be sent to search query).
    private ArrayList<CharSequence> mSelectedGenreIdList = new ArrayList<>();

    // Keeps track of genre names that are selected (purely for UI purposes).
    private ArrayList<CharSequence> mSelectedGenreNames = new ArrayList<>();

    // Specifies if an index in the genres list obtained from backend.
    private boolean[] mIsSelectedGenreIndex = new boolean[20];

    // The id's of all selected instruments (data that will be sent to search query).
    private ArrayList<CharSequence> mSelectedInstrumentIdList = new ArrayList<>();

    // Keeps track of instrument names that are selected (purely for UI purposes).
    private ArrayList<CharSequence> mSelectedInstrumentNames = new ArrayList<>();

    // Specifies if an index in the instrument list obtained from backend.
    private boolean[] mIsSelectedInstrumentIndex = new boolean[20];

    public UserSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment UserSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserSearchFragment newInstance() {
        UserSearchFragment fragment = new UserSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Used to show the selected instruments and genres that the user has previously chosen.
     * @param arrayNameList array of instrument or genre names.
     * @param selectedText text that will be displayed.
     */
    public void writeToSelectionText(ArrayList<CharSequence> arrayNameList, TextView selectedText) {
        // If user selected something before then add it to the selectedText.
        if (arrayNameList.size() > 0) {
            selectedText.setText("");
            for (int i = 0; i < arrayNameList.size(); i++) {
                selectedText.append(arrayNameList.get(i));
                if (!(i == arrayNameList.size() - 1)) {
                    selectedText.append(", ");
                }
            }
        }else {
            selectedText.setText(R.string.search_none_selected);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        writeToSelectionText(mSelectedInstrumentNames, mSelectedInstruments);
        writeToSelectionText(mSelectedGenreNames, mSelectedGenres);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_search, container, false);
        findViews(rootView);
        return rootView;
    }

    /**
     * Find all views to be manipulated and set them to private variables.
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
     * Display a dialog to show genres and allow user to select multiple.
     * @param v View which is calling this function
     */
    public void onShowGenres(View v){
        Log.d(TAG, "Show genres pushed!");
        mGenres.setEnabled(false);
        DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().getGenres(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                mGenres.setEnabled(true);
                createGenresDialog((JSONArray) response);
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                mGenres.setEnabled(true);
                Log.d(TAG, error.getMessage());
                Toast.makeText(getContext(),
                        "Oops, hit an unexpected error while fetching genres!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Display a dialog to show instruments and allow user to select multiple.
     * @param v view containing all instruments
     */
    public void onShowInstruments(View v) {
        Log.d(TAG, "Show instruments pushed!");
        mInstruments.setEnabled(false);
        DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().getInstruments(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                mInstruments.setEnabled(true);
                createInstrumentsDialog((JSONArray) response);
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                mInstruments.setEnabled(true);
                Log.d(TAG, error.getMessage());
                Toast.makeText(getContext(),
                        "Oops, hit an unexpected error while fetching instruments!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Create and display selection dialog made from the instruments listed in the response object.
     * @param response
     */
    private void createInstrumentsDialog(JSONArray response) {
        try {
            CharSequence[] itemNames = new CharSequence[response.length()];
            ArrayList<CharSequence> itemIds = new ArrayList<>();
            for (int i = 0; i < response.length(); i++) {
                JSONObject curr = response.getJSONObject(i);
                itemNames[i] = curr.getString("name");
                itemIds.add(curr.getString("_id"));
            }
            Log.d(TAG, "All instruments have been added to list!");

            Dialog instrumentDialog = createInstrumentsSelectionDialog(itemNames, itemIds);
            instrumentDialog.show();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create and display selection dialog made from the genres listed in the response object.
     * @param response an array of all genres.
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
            Log.d(TAG, "All genres have been added to list!");

            Dialog genresDialog = createGenresSelectionDialog(itemNames, itemIds);
            genresDialog.show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use dialog builder to create checkbox dialog from itemNames and select itemIds.
     * @param itemNames list of all instrument names to display.
     * @param itemIds list of all instrument id's to display (id index corresponds to name index).
     * @return
     */
    private Dialog createInstrumentsSelectionDialog(final CharSequence[] itemNames, final ArrayList<CharSequence> itemIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final ArrayList<CharSequence> backup = (ArrayList<CharSequence>) mSelectedInstrumentIdList.clone();
        builder.setTitle(R.string.search_select_instruments)
                .setMultiChoiceItems(itemNames, mIsSelectedInstrumentIndex, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // When instrument is selected or de-selected.
                        if (isChecked) {
                            // Add instrument id to selected.
                            mSelectedInstrumentIdList.add(itemIds.get(which));
                            mSelectedInstrumentNames.add(itemNames[which]);
                            mIsSelectedInstrumentIndex[which] = true;
                        } else if (mSelectedInstrumentIdList.contains(itemIds.get(which))) {
                            // Remove instrument from list.
                            mSelectedInstrumentIdList.remove(itemIds.get(which));
                            mSelectedInstrumentNames.remove(itemNames[which]);
                            mIsSelectedInstrumentIndex[which] = false;
                        }
                    }
                })
                .setPositiveButton(getString(R.string.search_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set text on instruments list element.
                        mSelectedInstruments.setText("");
                        for (int i = 0; i < mSelectedInstrumentNames.size(); i++) {
                            mSelectedInstruments.append(mSelectedInstrumentNames.get(i));
                            if (!(i == mSelectedInstrumentNames.size() - 1)) {
                                mSelectedInstruments.append(", ");
                            }
                        }
                    }
                })
                .setNegativeButton(getString(R.string.search_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Restore selected instruments to previous state.
                        mSelectedInstrumentIdList = backup;
                    }
                });

        return builder.create();
    }

    /**
     * Use dialog builder to create checkbox dialog from itemNames and select itemIds.
     * @param itemNames list of all genre names to display
     * @param itemIds list of all genre id's to display (id index corresponds to name index)
     * @return dialog
     */
    private Dialog createGenresSelectionDialog(final CharSequence[] itemNames, final ArrayList<CharSequence> itemIds) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        final ArrayList<CharSequence> backup = (ArrayList<CharSequence>) mSelectedGenreIdList.clone();
        builder.setTitle("Select genres")
                .setMultiChoiceItems(itemNames, mIsSelectedGenreIndex, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        // When genre is selected or de-selected.
                        if (isChecked) {
                            // Add genre id to selected
                            mSelectedGenreIdList.add(itemIds.get(which));
                            mSelectedGenreNames.add(itemNames[which]);
                            mIsSelectedGenreIndex[which] = true;
                        } else if (mSelectedGenreIdList.contains(itemIds.get(which))) {
                            // Remove genre from list
                            mSelectedGenreIdList.remove(itemIds.get(which));
                            mSelectedGenreNames.remove(itemNames[which]);
                            mIsSelectedGenreIndex[which] = false;
                        }
                    }
                })
                .setPositiveButton(R.string.search_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Set text on genre list element.
                        mSelectedGenres.setText("");
                        for (int i = 0; i < mSelectedGenreNames.size(); i++) {
                            mSelectedGenres.append(mSelectedGenreNames.get(i));
                            if (!(i == mSelectedGenreNames.size() - 1)) {
                                mSelectedGenres.append(", ");
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.search_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // restore selected genres to previous state
                        mSelectedGenreIdList = backup;
                    }
                });

        return builder.create();
    }

    /**
     * Take all parameters from form and send search request.
     * @param view
     */
    public void onClickSearch(View view){
        Log.d(TAG, "Search initialized");
        JSONObject queryObject = makeQueryJson();
        makeQuery(queryObject);
    }

    /**
     * Make and send request to server and respond to results.
     * @param queryObject JSON query for server
     */
    private void makeQuery(JSONObject queryObject) {
        DatabaseSingleton.getInstance(getContext()).getBandUpDatabase().getSearchQuery(queryObject,
                new BandUpResponseListener() {
                    @Override
                    public void onBandUpResponse(Object response) {

                        // create userlist and instantiate fragment with new list
                        try{
                            JSONObject rsp = (JSONObject)response;
                            JSONArray userArr = rsp.getJSONArray("result");
                            FragmentManager fragmentManager = getFragmentManager();
                            User[] users = new User[userArr.length()];
                            for (int i = 0; i < userArr.length(); i++){
                                users[i] = new User(userArr.getJSONObject(i));
                            }
                            UserListFragment us = ((MainScreenActivity)getActivity()).startSearchResults(users);

                            fragmentManager.beginTransaction()
                                            .replace(R.id.mainFrame, us)
                                            .commit();
                        } catch (JSONException ex){
                            ex.printStackTrace();
                        }

                    }
                }, new BandUpErrorListener() {
                    @Override
                    public void onBandUpErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
    }

    /**
     * Take all information from form and if not in default value state insert into query.
     * @return JSONObject that represents our query
     */
    private JSONObject makeQueryJson() {
        // get values
        String username = mUsername.getText().toString();
        int minAge = mSeekBarAges.getSelectedMinValue().intValue();
        int maxAge = mSeekBarAges.getSelectedMaxValue().intValue();

        // construct JSON
        JSONObject query = new JSONObject();
        try{
            if (!username.equals("")){
                JSONObject regex = new JSONObject();
                // get all users with username as substring
                regex.put("$regex", username);
                // make search case insensitive
                regex.put("$options", "i");
                query.put("username", regex);
            }
            // set genre and instrument selection
            if (mSelectedGenreIdList.size() > 0){
                JSONObject elemMatch = new JSONObject();
                JSONArray genres = new JSONArray();
                for (int i = 0; i < mSelectedGenreIdList.size(); i++){
                    genres.put(mSelectedGenreIdList.get(i));
                }
                elemMatch.put("$in", genres);
                query.put("genres", elemMatch);
            }

            if(mSelectedInstrumentIdList.size() > 0){
                JSONObject elemMatch = new JSONObject();
                JSONArray instruments = new JSONArray();
                for (int i = 0; i < mSelectedInstrumentIdList.size(); i++){
                    instruments.put(mSelectedInstrumentIdList.get(i));
                }
                elemMatch.put("$in", instruments);
                query.put("instruments", elemMatch);
            }

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");


            // set age ranges
            Calendar minDate = Calendar.getInstance();
            minDate.setTime(new Date());
            minDate.add(Calendar.YEAR, -minAge);

            Calendar maxDate = Calendar.getInstance();
            maxDate.setTime(new Date());
            maxDate.add(Calendar.YEAR, -maxAge);

            JSONObject dateSelect = new JSONObject();
            dateSelect.put("$lte", df.format(minDate.getTime()));
            dateSelect.put("$gte", df.format(maxDate.getTime()));

            query.put("dateOfBirth", dateSelect);
        }catch (JSONException ex){
            ex.printStackTrace();
        }
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
