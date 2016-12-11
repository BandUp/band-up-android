package com.melodies.bandup.MainScreenActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.LocaleSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.SoundCloudFragments.SoundCloudPlayerFragment;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.locale.LocaleRules;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {
    private User currentUser;

    private OnFragmentInteractionListener mListener;


    public UserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailsFragment newInstance() {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private TextView txtName;
    private TextView txtAge;
    private TextView txtFavorite;
    private TextView txtPercentage;
    private TextView txtDistance;
    private TextView txtAboutMe;
    private TextView txtInstrumentsTitle;
    private TextView txtGenresTitle;
    private TextView txtInstrumentsList;
    private TextView txtGenresList;
    private TextView txtSoundCloudExample;
    private TextView txtNoSoundCloudExample;
    private ImageView ivUserProfileImage;
    private Button btnLike;
    private AdView mAdView;
    private LinearLayout mSoundcloudArea;

    private void initializeViews(View rootView) {
        txtName = (TextView) rootView.findViewById(R.id.txtName);
        txtDistance = (TextView) rootView.findViewById(R.id.txtDistance);
        txtPercentage = (TextView) rootView.findViewById(R.id.txtPercentage);
        txtAge = (TextView) rootView.findViewById(R.id.txtAge);
        txtFavorite = (TextView) rootView.findViewById(R.id.txtFavorite);
        txtAboutMe = (TextView) rootView.findViewById(R.id.txtAboutMe);
        txtInstrumentsTitle = (TextView) rootView.findViewById(R.id.txtInstrumentTitle);
        txtGenresTitle = (TextView) rootView.findViewById(R.id.txtGenresTitle);
        txtInstrumentsList = (TextView) rootView.findViewById(R.id.txtInstrumentsList);
        txtGenresList = (TextView) rootView.findViewById(R.id.txtGenresList);
        txtSoundCloudExample = (TextView) rootView.findViewById(R.id.txt_audio_example);
        ivUserProfileImage = (ImageView) rootView.findViewById(R.id.imgProfile);
        btnLike = (Button) rootView.findViewById(R.id.btnLike);
        mAdView = (AdView) rootView.findViewById(R.id.adView);
        mSoundcloudArea = (LinearLayout) rootView.findViewById(R.id.soundcloud_player_area);
        txtFetchError = (TextView) rootView.findViewById(R.id.txtFetchError);
        txtNoSoundCloudExample = (TextView) rootView.findViewById(R.id.no_soundcloud_example);


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainScreenActivity) getActivity()).onClickLike(currentUser.id);
            }
        });
    }

    private void setFonts() {
        Typeface caviarDreams = Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf");
        Typeface caviarDreamsBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams_bold.ttf");
        Typeface masterOfBreak = Typeface.createFromAsset(getActivity().getAssets(), "fonts/master_of_break.ttf");


        txtName.setTypeface(caviarDreams);
        txtDistance.setTypeface(caviarDreams);
        txtPercentage.setTypeface(caviarDreams);
        txtAge.setTypeface(caviarDreams);
        txtFavorite.setTypeface(caviarDreams);
        txtAboutMe.setTypeface(caviarDreams);
        txtInstrumentsList.setTypeface(caviarDreams);
        txtGenresList.setTypeface(caviarDreams);
        txtInstrumentsTitle.setTypeface(caviarDreamsBold);
        txtGenresTitle.setTypeface(caviarDreamsBold);
        btnLike.setTypeface(masterOfBreak);
        txtNoSoundCloudExample.setTypeface(caviarDreamsBold);
        txtSoundCloudExample.setTypeface(caviarDreamsBold);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
        // Gets the user_id from userListFragment

    }

    private TextView txtFetchError;
    private ProgressBar progressBar;
    private LinearLayout llProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainScreenActivity mainScreenActivity = (MainScreenActivity)getActivity();
        mainScreenActivity.currentFragment = mainScreenActivity.USER_DETAILS_FRAGMENT;
        mainScreenActivity.invalidateOptionsMenu();

        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        initializeViews(rootView);
        setFonts();

        progressBar = (ProgressBar) rootView.findViewById(R.id.userListProgressBar);
        llProfile = (LinearLayout) rootView.findViewById(R.id.ll_profile);

        String argumentUserID = getArguments().getString("user_id");

        if (currentUser == null || !currentUser.id.equals(argumentUserID)) {
            fetchCurrentUser(getArguments().getString("user_id"));
        } else {
            populateUser(currentUser);
        }

        return rootView;
    }

    private void populateUser(User u) {
        // Adding ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        LocaleRules localeRules = LocaleSingleton.getInstance(getActivity()).getLocaleRules();
        if (u.imgURL != null) {
            Picasso.with(getActivity()).load(u.imgURL).into(ivUserProfileImage);
        }

        txtName.setText(u.name);
        if (localeRules != null) {
            Integer age = u.ageCalc();
            if (age != null) {
                if (localeRules.ageIsPlural(age)) {
                    String ageString = String.format("%s %s", age, getString((R.string.age_year_plural)));
                    txtAge.setText(ageString);
                } else {
                    String ageString = String.format("%s %s", age, getString((R.string.age_year_singular)));
                    txtAge.setText(ageString);
                }
            } else {
                txtAge.setText("Age not available");
            }
        }
        if (u.favoriteinstrument != null && !u.favoriteinstrument.equals("")) {
            txtFavorite.setText(u.favoriteinstrument);
        } else {
            if (u.instruments.size() != 0) {
                txtFavorite.setText(u.instruments.get(0));
            }
        }
        txtPercentage.setText(u.percentage + "%");
        txtAboutMe.setText(u.aboutme);

        if (u.location != null) {
            Float distanceBetweenUsers = getDistanceToUser(currentUser);
            if (distanceBetweenUsers != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
                Boolean usesImperial = sharedPreferences.getBoolean("switchUnit", false);
                if (usesImperial) {
                    String distanceString = String.format("%s %s", (int) Math.ceil(kilometersToMiles(distanceBetweenUsers / 1000)), getString(R.string.mi_distance));
                    txtDistance.setText(distanceString);
                } else {
                    String distanceString = String.format("%s %s", (int) Math.ceil(distanceBetweenUsers / 1000), getString(R.string.km_distance));
                    txtDistance.setText(distanceString);
                }
            } else {
                txtDistance.setText(R.string.no_distance_available);
            }
        } else {
            txtDistance.setText(R.string.no_distance_available);
        }

        for (int i = 0; i < u.genres.size(); i++) {
            txtGenresList.append(u.genres.get(i) + "\n");
        }

        for (int i = 0; i < u.instruments.size(); i++) {
            txtInstrumentsList.append(u.instruments.get(i) + "\n");
        }

        createSoundCloudPlayer(u);
        llProfile.setVisibility(View.VISIBLE);


        txtNoSoundCloudExample.setText(String.format("%s %s", u.name, getString(R.string.no_soundcloud_example)));
        if (u.id.equals(currentUser.id)) {

        }
    }

    private Float getDistanceToUser(User u) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        String locationProvider = ((MainScreenActivity) getActivity()).bestProvider;
        Boolean hasLocationPermission = ((MainScreenActivity) getActivity()).hasLocationPermission();
        if (hasLocationPermission) {

            Location myLocation = locationManager.getLastKnownLocation(locationProvider);
            Location userLocation = new Location("");
            if (u.location.getValid()) {
                userLocation.setLatitude(u.location.getLatitude());
                userLocation.setLongitude(u.location.getLongitude());
            } else {
                return null;
            }

            if (myLocation != null) {
                return myLocation.distanceTo(userLocation);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private void createSoundCloudPlayer(User user) {

        if (user.soundCloudURL == null || user.soundCloudURL.equals("")){
            txtNoSoundCloudExample.setVisibility(View.VISIBLE);
        } else {
            txtNoSoundCloudExample.setVisibility(View.GONE);
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            mSoundcloudArea.setId(Integer.valueOf(1234));
            Fragment soundCloudFragment;

            soundCloudFragment = SoundCloudPlayerFragment.newInstance(user.soundCloudURL);
            ft.replace(mSoundcloudArea.getId(), soundCloudFragment, "soundCloudFragment");
            ft.commit();
        }



    }

    // Request REAL user info from server
    public void fetchCurrentUser(String userid) {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        llProfile.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseSingleton.getInstance(getActivity()).getBandUpDatabase().getUserProfile(user, new BandUpResponseListener() {

            @Override
            public void onBandUpResponse(Object response) {
                if (getActivity() == null){
                    return;
                }
                progressBar.setVisibility(View.INVISIBLE);
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                } else {
                    txtFetchError.setVisibility(View.VISIBLE);
                }
                if (responseObj != null) {
                    // Binding View to real data
                    currentUser = new User(responseObj);
                    populateUser(currentUser);

                }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                if (getActivity() == null){
                    return;
                }
                txtFetchError.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private int kilometersToMiles(double miles) {
        return (int) Math.round(miles / 1.609344);
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
