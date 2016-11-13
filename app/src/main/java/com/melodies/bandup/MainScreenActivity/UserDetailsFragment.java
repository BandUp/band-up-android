package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.LocaleSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.SoundCloudFragments.SoundCloudLoginFragment;
import com.melodies.bandup.SoundCloudFragments.SoundCloudPlayerFragment;
import com.melodies.bandup.SoundCloudFragments.SoundCloudSelectorFragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private User currentUser;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDetailsFragment newInstance(String param1, String param2) {
        UserDetailsFragment fragment = new UserDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView    txtName;
    private TextView    txtAge;
    private TextView    txtFavorite;
    private TextView    txtPercentage;
    private TextView    txtDistance;
    private TextView    txtAboutMe;
    private TextView    txtInstrumentsTitle;
    private TextView    txtGenresTitle;
    private TextView    txtInstrumentsList;
    private TextView    txtGenresList;
    private ImageView   ivUserProfileImage;
    private Button      btnLike;
    private AdView      mAdView;
    private LinearLayout mSoundcloudArea;

    private void initializeViews(View rootView) {
        txtName             = (TextView)     rootView.findViewById(R.id.txtName);
        txtDistance         = (TextView)     rootView.findViewById(R.id.txtDistance);
        txtPercentage       = (TextView)     rootView.findViewById(R.id.txtPercentage);
        txtAge              = (TextView)     rootView.findViewById(R.id.txtAge);
        txtFavorite         = (TextView)     rootView.findViewById(R.id.txtFavorite);
        txtAboutMe          = (TextView)     rootView.findViewById(R.id.txtAboutMe);
        txtInstrumentsTitle = (TextView)     rootView.findViewById(R.id.txtInstrumentTitle);
        txtGenresTitle      = (TextView)     rootView.findViewById(R.id.txtGenresTitle);
        txtInstrumentsList  = (TextView)     rootView.findViewById(R.id.txtInstrumentsList);
        txtGenresList       = (TextView)     rootView.findViewById(R.id.txtGenresList);
        ivUserProfileImage  = (ImageView)    rootView.findViewById(R.id.imgProfile);
        btnLike             = (Button)       rootView.findViewById(R.id.btnLike);
        mAdView             = (AdView)       rootView.findViewById(R.id.adView);
        mSoundcloudArea     = (LinearLayout) rootView.findViewById(R.id.soundcloud_player_area);

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainScreenActivity)getActivity()).onClickLike(currentUser.id);
            }
        });
    }

    private void setFonts() {
        Typeface caviarDreams     = Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf");
        Typeface caviarDreamsBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams_bold.ttf");
        Typeface masterOfBreak    = Typeface.createFromAsset(getActivity().getAssets(), "fonts/master_of_break.ttf");

        txtName            .setTypeface(caviarDreams);
        txtDistance        .setTypeface(caviarDreams);
        txtPercentage      .setTypeface(caviarDreams);
        txtAge             .setTypeface(caviarDreams);
        txtFavorite        .setTypeface(caviarDreams);
        txtAboutMe         .setTypeface(caviarDreams);
        txtInstrumentsList .setTypeface(caviarDreams);
        txtGenresList      .setTypeface(caviarDreams);
        txtInstrumentsTitle.setTypeface(caviarDreamsBold);
        txtGenresTitle     .setTypeface(caviarDreamsBold);
        btnLike            .setTypeface(masterOfBreak);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Gets the user_id from userListFragment

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);

        initializeViews(rootView);
        setFonts();


        String argumentUserID = getArguments().getString("user_id");

        if (currentUser == null || !currentUser.id.equals(argumentUserID)) {
            fetchCurrentUser(getArguments().getString("user_id"));
        } else {
            System.out.println(currentUser.id);
            displayUser(currentUser);
        }

        return rootView;
    }

    private void displayUser(User u) {
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
            }
        }
        txtFavorite.setText("Drums");
        txtPercentage.setText(u.percentage + "%");
        txtAboutMe.setText(u.aboutme);

        if (txtDistance == null) {
            txtDistance.setText(u.distance + " km away from you");
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
    }

    private void createSoundCloudPlayer(User user) {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        mSoundcloudArea.setId(new Integer(1234));
        Fragment soundCloudFragment;
        if (user.soundCloudId == 0){
            soundCloudFragment = SoundCloudPlayerFragment.newInstance(null);
        }else {
            soundCloudFragment = SoundCloudPlayerFragment.newInstance(user.soundCloudURL);
        }

        ft.add(mSoundcloudArea.getId(), soundCloudFragment, "soundCloudFragment");
        ft.commit();

    }

    // Request REAL user info from server
    public void fetchCurrentUser(String userid) {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", userid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance(getActivity()).getBandUpDatabase().getUserProfile(user, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                }
                if (response != null) {
                    // Binding View to real data
                    currentUser = new User();
                    try {
                        if (!responseObj.isNull("_id")) {
                            currentUser.id = responseObj.getString("_id");
                        }
                        if (!responseObj.isNull("username")) {
                            currentUser.name = responseObj.getString("username");
                        }
                        if (!responseObj.isNull("dateOfBirth")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            currentUser.dateOfBirth = df.parse(responseObj.getString("dateOfBirth"));
                        }

                        if (!responseObj.isNull("distance")) {
                            currentUser.distance = responseObj.getInt("distance");
                        } else {
                            currentUser.distance = null;
                        }

                        if (!responseObj.isNull("percentage")) {
                            currentUser.percentage = responseObj.getInt("percentage");
                        }

                        if (!responseObj.isNull("genres")) {
                            JSONArray genreArray = responseObj.getJSONArray("genres");
                            for (int i = 0; i < genreArray.length(); i++) {
                                currentUser.genres.add(genreArray.getString(i));
                            }
                        }

                        if (!responseObj.isNull("instruments")) {
                            JSONArray instrumentArray = responseObj.getJSONArray("instruments");
                            for (int i = 0; i < instrumentArray.length(); i++) {
                                currentUser.instruments.add(instrumentArray.getString(i));
                            }
                        }

                        if (!responseObj.isNull("aboutme")) {
                            currentUser.aboutme = responseObj.getString("aboutme");
                        }

                        if (!responseObj.isNull("image")) {
                            JSONObject imageObj = responseObj.getJSONObject("image");

                            if (!imageObj.isNull("url")) {
                                currentUser.imgURL = imageObj.getString("url");
                            }
                        }
                        displayUser(currentUser);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                System.out.println("ERROR");
            }
        });
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
