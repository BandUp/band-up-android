package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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
    private static final int MINAGE = 13;
    private static final int MAXAGE = 99;
    private static final int DEFAULTAGE = 20;   // first default value for age and range

    private OnFragmentInteractionListener mListener;

    private static int   searchRadius = 0;
    private TextView     txtRadius;
    private SeekBar      seekBarRadius;
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
        unitInit(rootView);
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

    // Adding ad Banner
    private void adInit(View rootView) {
        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // saving and updating unit state
    private void unitInit(View rootView) {
        switchUnit = (Switch)rootView.findViewById(R.id.switchUnit);
        // loading user switch criteria
        switchUnit.setChecked(loadUserSwitch("switchUnit"));

        switchUnit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                seekBarRadius.setMax(300);
                if (switchUnit.isChecked()) {
                    saveSwitchState("switchUnit", true);
                    String textmi = String.format("Radius: %s %s", kilometersToMiles(loadUserCredentials("searchradius")), "mi");
                    txtRadius.setText(textmi);

                }
                else {
                    saveSwitchState("switchUnit", false);

                    String textkm = String.format("Radius: %s %s", loadUserCredentials("searchradius"), "km");
                    txtRadius.setText(textkm);

                }
            }
        });
    }

    // if switch Inactive Alert is checked send notifications to user, else do not
    public void notificationAlertInit(View rootView) {
        switchAlert = (Switch)rootView.findViewById(R.id.switchAlert);
        // loading user switch criteria it's always on(true) by default
        switchAlert.setChecked(loadUserSwitch("switchAlert"));

        switchAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // saving new states
                if (switchAlert.isChecked()) {
                    saveSwitchState("switchAlert", true);
                }
                else {
                    saveSwitchState("switchAlert", false);
                }
            }
        });
    }

    // if switch messages is checked send notifications to user about new message, else do not
    public void notificationMessagesInit(View rootView) {
        switchMessages = (Switch)rootView.findViewById(R.id.switchMessages);
        // loading user switch criteria by default it's always on
        switchMessages.setChecked(loadUserSwitch("switchMessages"));

        switchMessages.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // saving new states
                if (switchMessages.isChecked()) {
                    saveSwitchState("switchMessages", true);
                }
                else {
                    saveSwitchState("switchMessages", false);
                }
            }
        });

    }

    // if switch matches is checked send notifications to user about new matches, else do not
    public void notificationMatchesInit(View rootView) {
        switchMatches = (Switch)rootView.findViewById(R.id.switchMatches);
        // loading user switch criteria
        switchMatches.setChecked(loadUserSwitch("switchMatches"));

        switchMatches.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // saving new states
                if (switchMatches.isChecked()) {
                    saveSwitchState("switchMatches", true);
                }
                else {
                    saveSwitchState("switchMatches", false);
                }
            }
        });
    }

    // initializing min and max age, and listen for user criteria
    private void ageInit(View rootView) {
        RangeSeekBar seekBarAges = (RangeSeekBar) rootView.findViewById(R.id.seekBarAges);

        seekBarAges.setRangeValues(MINAGE, MAXAGE);     // search range
        seekBarAges.setSelectedMinValue(loadUserCredentials("minAge"));
        seekBarAges.setSelectedMaxValue(loadUserCredentials("maxAge"));

        seekBarAges.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minAge, Integer maxAge) {
                // TODO: store searchrangeAge in DB||SharedPreferences which is better?
                saveUserSearchRange("minAge", minAge);
                saveUserSearchRange("maxAge", maxAge);
            }
        });
    }
    // ============================== START Shared Preferences SAVING AND LOADING ===================================================
    // Saving user credentials: age and searchrangeon on User Phone
    public void saveUserSearchRange(String valueName, Integer value) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileAge", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(valueName, value);
        editor.apply();
    }

    // saving switch state
    public void saveSwitchState(String valueName, boolean value) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(valueName, value);
        editor.apply();
    }

    // Loading user credentials
    public Integer loadUserCredentials(String valueName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileAge", Context.MODE_PRIVATE);
        if (valueName == "minAge") {
            return sharedPreferences.getInt (valueName, MINAGE);    // This is default value for age, and range
        } else if (valueName == "maxAge"){
            return sharedPreferences.getInt (valueName, MAXAGE);    // This is default value for age, and range
        } else {
            return sharedPreferences.getInt(valueName, DEFAULTAGE);
        }
    }

    // loading switch state
    public boolean loadUserSwitch(String valueName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(valueName, true);   // this true is the default value
    }

    // ============================== END Shared Preferences SAVING AND LOADING ===================================================


    // all behaviour of radiusBarUnit is placed in this function
    private void radiusInit(View rootView) {
        txtRadius = (TextView)rootView.findViewById(R.id.txtRadius);
        seekBarRadius = (SeekBar)rootView.findViewById(R.id.seekBarRadius);
        // Default values
        seekBarRadius.setProgress(loadUserCredentials("searchradius"));
        seekBarRadius.setMax(300);
        if (loadUserSwitch("switchUnit")) {
            String textmi = String.format("Radius: %s %s", kilometersToMiles(loadUserCredentials("searchradius")), "mi");
            txtRadius.setText(textmi);
        }
        else {
            String textkm = String.format("Radius: %s %s", loadUserCredentials("searchradius"), "km");
            txtRadius.setText(textkm);
        }

        seekBarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarRadius.setMax(300);      // Maximum value of search range in Km

                // if unit switch is on, put mi in text view else km
                if (loadUserSwitch("switchUnit")) {
                          // Maximum value of search range in Mi
                    String radius = String.format("Radius: %s %s", kilometersToMiles(progress), "mi");
                    txtRadius.setText(radius);
                    searchRadius = progress;
                }
                else {
                    String radius = String.format("Radius: %s %s", progress, "km");
                    txtRadius.setText(radius);
                    searchRadius = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // store searchRadius into server and shared Prefs for display
                saveUserSearchRange("searchradius", searchRadius);
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
    private int kilometersToMiles(int miles) {
        double kilometers = miles / 1.609344;
        return (int) Math.ceil(kilometers);
    }

    public void onClickContact (View view) {
        Intent contact = new Intent(getActivity(), Contact.class);
        startActivity(contact);
    }

    public void onClickPrivacyPolicy (View view) {
        Intent privacy = new Intent(getActivity(), PrivacyPolicy.class);
        startActivity(privacy);
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
