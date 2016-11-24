package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.yahoo.mobile.client.android.util.rangeseekbar.RangeSeekBar;

import org.json.JSONException;
import org.json.JSONObject;

import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static int   searchRadius = 0;
    private AdView       mAdView;
    private TextView     txtRadius;
    private SeekBar      seekBarRadius;
    private RangeSeekBar seekBarAges;
    private Switch       switchMatches;
    private Switch       switchMessages;
    private Switch       switchAlert;
    private Switch       switchUnit;
    private TextView     txtContact;
    private TextView     txtHelp;
    private TextView     txtSupport;
    private TextView     txtLegal;
    private TextView     txtLicenses;
    private TextView     txtPPolicy;
    private TextView     txtTermsOfService;


    // Required empty public constructor
    public SettingsFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        initializeViews(rootView);

        return rootView;
    }

    // initializing all Views
    private void initializeViews(View rootView) {
        adInit(rootView);
        radiusInit(rootView);
        ageInit(rootView);
        notificationMatchesInit(rootView);
        notificationMessagesInit(rootView);
        notificationAlertInit(rootView);
        txtContact        = (TextView)rootView.findViewById(R.id.txtContact);
        txtHelp           = (TextView)rootView.findViewById(R.id.txtHelp);
        txtSupport        = (TextView)rootView.findViewById(R.id.txtSupport);
        txtLegal          = (TextView)rootView.findViewById(R.id.txtLegal);
        txtLicenses       = (TextView)rootView.findViewById(R.id.txtLicenses);
        txtPPolicy        = (TextView)rootView.findViewById(R.id.txtPPolicy);
        txtTermsOfService = (TextView)rootView.findViewById(R.id.txtTermsOfService);
    }

    // if switch Inactive Alert is checked send notifications to user, else do not
    private void notificationAlertInit(View rootView) {
        switchAlert = (Switch)rootView.findViewById(R.id.switchAlert);
        if (switchAlert.isChecked()) {
            //TODO: send notification
        }
        else {
            //TODO: don't send any notifications
        }
    }

    // if switch messages is checked send notifications to user about new message, else do not
    private void notificationMessagesInit(View rootView) {
        switchMessages = (Switch)rootView.findViewById(R.id.switchMessages);
        if (switchMessages.isChecked()) {
            //TODO: send notification to user about new message
        }
        else {
            //TODO: don't send any new message notification to user
        }
    }

    // if switch matches is checked send notifications to user about new matches, else do not
    private void notificationMatchesInit(View rootView) {
        switchMatches = (Switch)rootView.findViewById(R.id.switchMatches);
        if (switchMatches.isChecked()) {
            //TODO: send notification to user about new matches
        }
        else {
            //TODO: don't send any new matces notification to user
        }
    }

    // Adding ad Banner
    private void adInit(View rootView) {
        mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // initializing min and max age, and listen for user criteria
    private void ageInit(View rootView) {
        seekBarAges = (RangeSeekBar) rootView.findViewById(R.id.seekBarAges);
        RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(getActivity());
        seekBarAges.setRangeValues(13, 99);     // min and max age

        seekBarAges.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minAge, Integer maxAge) {
                // TODO: store searchrangeAge in DB||SharedPreferences which is better?
                saveUserCredentials("minAge", minAge);
                saveUserCredentials("maxAge", maxAge);
            }
        });
    }

    // Saving user credentials on User Phone
    public void saveUserCredentials(String valueName, Integer value) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(valueName, value);
        editor.apply();
        Toast.makeText(getActivity(), "Saved: "+value , Toast.LENGTH_SHORT).show();
    }

    // Loading user credentials
    public Integer loadUserCredentials(String valueName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFile", Context.MODE_PRIVATE);
        return Integer.valueOf(sharedPreferences.getString(valueName, DEFAULT));
    }

    // all behaviour of radiusBarUnit is placed in this function
    private void radiusInit(View rootView) {
        switchUnit = (Switch)rootView.findViewById(R.id.switchUnit);
        txtRadius = (TextView)rootView.findViewById(R.id.txtRadius);
        seekBarRadius = (SeekBar)rootView.findViewById(R.id.seekBarRadius);
        seekBarRadius.setProgress(25);      // Default progress value

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // if unit switch is on, put mi in text view else km
                if (switchUnit.isChecked()) {
                    seekBarRadius.setMax(186);      // Maximum value of search range in Mi
                    String radius = String.format("%s %s %s", "Radius ", Integer.toString(progress), " Mi");
                    txtRadius.setText(radius);
                    searchRadius = milesToKilometers(progress);
                }
                else {
                    seekBarRadius.setMax(300);      // Maximum value of search range in Km
                    String radius = String.format("%s %s %s", "Radius ", Integer.toString(progress), " Km");
                    txtRadius.setText(radius);
                    searchRadius = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // store searchRadius into server in one consistent unit / (let's say km)
                try {
                    updateUser(getUserId(), "searchradius", searchRadius);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Get the userid of logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getActivity().getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String id = srdPref.getString("userID", DEFAULT);
        return (!id.equals(DEFAULT)) ? id : "User ID Not Found";
    }

    // Send updated user data to server, takes current User ID, mongo Schema Attr, and data to be updated
    public void updateUser(String id, String schemaAttr, final int data) {
        JSONObject userUpdated = new JSONObject();
        try {
            userUpdated.put("_id", id);
            userUpdated.put(schemaAttr, data);

            DatabaseSingleton.getInstance(getActivity()).getBandUpDatabase().updateUser(userUpdated, new BandUpResponseListener() {
                @Override
                public void onBandUpResponse(Object response) {
                    // succesful response
                    Toast.makeText(getActivity(), "Data updated! REMOVE THIS TOAST", Toast.LENGTH_SHORT).show();
                }
            }, new BandUpErrorListener() {
                @Override
                public void onBandUpErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getActivity(), "Error" + error, Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Converts miles into whole kilometers for consistent search range storage
    private int milesToKilometers(int miles) {
        double kilometers = miles * 1.609344;
        return (int) Math.ceil(kilometers);
    }

    // is called whenever we attach fragment to activity
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


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }
}
