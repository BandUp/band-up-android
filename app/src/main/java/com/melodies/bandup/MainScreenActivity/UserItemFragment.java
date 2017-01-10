package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.LocaleSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.locale.LocaleRules;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.LOCATION_SERVICE;

public class UserItemFragment extends Fragment {
    int mNum;

    private TextView txtName, txtDistance, txtInstruments, txtGenres, txtPercentage, txtAge;
    private Button btnLike, btnDetails;
    private ImageView ivUserProfileImage;
    private AdView mAdView;
    private ImageView animCheck;

    private User mUser;
    private LinearLayout networkErrorBar;

    /**
     * Create a new instance of the UserItemFragment
     * @param num the index of the user in the list
     * @param user the user itself.
     * @return the fragment
     */
    public static UserItemFragment newInstance(int num, User user) {
        UserItemFragment f = new UserItemFragment();

        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putSerializable("user", user);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }
        // Get the arguments from when the fragment was created.
        mUser = (User) (getArguments() != null ? getArguments().getSerializable("user") : 1);
        mNum = (getArguments() != null ? getArguments().getInt("num") : 1);
    }

    private void initializeTextViews(View rootView) {
        ivUserProfileImage = (ImageView) rootView.findViewById(R.id.imgProfile);
        txtName            = (TextView)  rootView.findViewById(R.id.txtName);
        txtInstruments     = (TextView)  rootView.findViewById(R.id.txtMainInstrument);
        txtGenres          = (TextView)  rootView.findViewById(R.id.txtGenres);
        txtDistance        = (TextView)  rootView.findViewById(R.id.txtDistance);
        txtPercentage      = (TextView)  rootView.findViewById(R.id.txtPercentage);
        txtAge             = (TextView)  rootView.findViewById(R.id.txtAge);
        mAdView            = (AdView)    rootView.findViewById(R.id.adView);
    }

    private void initializeButtons(View rootView) {
        btnLike     = (Button)    rootView.findViewById(R.id.btnLike);
        networkErrorBar = (LinearLayout) getActivity().findViewById(R.id.network_connection_error_bar);

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (networkErrorBar.getVisibility() == View.INVISIBLE) {
                    onClickLike(mUser, v);
                    //ViewPager pager = ((UserListFragment)getParentFragment()).mPager;

                    //if (pager.getCurrentItem() != pager.getAdapter().getCount() - 1) {

                        //pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                    //}
                }
            }
        });

        btnDetails  = (Button)    rootView.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainScreenActivity) getActivity()).onClickDetails(v, mNum);
            }
        });

        ivUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainScreenActivity) getActivity()).onClickDetails(v, mNum);
            }
        });
    }

    private void setFonts() {
        Typeface caviarDreams = Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf");
        Typeface masterOfBreak = Typeface.createFromAsset(getActivity().getAssets(), "fonts/master_of_break.ttf");

        txtName       .setTypeface(caviarDreams);
        txtInstruments.setTypeface(caviarDreams);
        txtGenres     .setTypeface(caviarDreams);
        txtDistance   .setTypeface(caviarDreams);
        txtPercentage .setTypeface(caviarDreams);
        txtAge        .setTypeface(caviarDreams);

        btnLike   .setTypeface(masterOfBreak);
        btnDetails.setTypeface(masterOfBreak);
    }

    public void onClickLike(final User user, final View likeButton) {
        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.put("userID", user.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance(getActivity().getApplicationContext()).getBandUpDatabase().postLike(jsonUser, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                networkErrorBar.setVisibility(View.INVISIBLE);
                JSONObject responseObj = null;

                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                } else {
                    return;
                }
                try {

                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.pop);
                    animCheck.setVisibility(View.VISIBLE);
                    animCheck.startAnimation(animation);
                    Boolean isMatch;
                    if (!responseObj.isNull("isMatch")) {
                        isMatch = responseObj.getBoolean("isMatch");
                    } else {
                        Toast.makeText(getActivity(), R.string.main_error_match, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (likeButton instanceof Button) {
                        Button likeBtn = (Button) likeButton;
                        likeBtn.setText(getString(R.string.user_list_liked));
                        likeBtn.setEnabled(false);
                        likeBtn.setBackgroundResource(R.drawable.button_user_list_like_disabled);
                    }
                    user.isLiked = true;

                    ((MainScreenActivity)getActivity()).userListFragment.mAdapter.likeUserById(user.id);

                    if (isMatch) {
                        Toast.makeText(getActivity(), R.string.main_matched, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    networkErrorBar.setVisibility(View.VISIBLE);
                    return;
                }

                // TODO: Add OnClickListener.

                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);

            }
        });
    }


    /*
     * Set up the view for the fragment.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_user_partial, container, false);
        initializeTextViews(rootView);
        initializeButtons(rootView);
        setFonts();
        animCheck = (ImageView) rootView.findViewById(R.id.animated_check);
        populateUser(mUser);

        return rootView;
    }

    /**
     * Displays the user 'u' in the fragment
     * @param u
     */
    private void populateUser(User u) {
        if (getActivity() == null) {
            return;
        }
        LocaleRules localeRules = LocaleSingleton.getInstance(getActivity()).getLocaleRules();
        User authUser = ((MainScreenActivity)getActivity()).currentUser;
        txtName.setText(u.name);

        if (u.favoriteinstrument != null && !u.favoriteinstrument.equals("")) {
            txtInstruments.setText(u.favoriteinstrument);
        }
        else {
            if (u.instruments.size() != 0) {
                txtInstruments.setText(u.instruments.get(0));
            }
        }

        if (u.genres.size() > 0) {
            if (u.instruments.size() != 0) {
                txtGenres.setText(u.genres.get(0));
            }
        }

        txtPercentage.setText(u.percentage + "%");


        if (u.isLiked) {
            btnLike.setText(getString(R.string.user_list_liked));
            btnLike.setEnabled(false);
            btnLike.setBackgroundResource(R.drawable.button_user_list_like_disabled);
            animCheck.setVisibility(View.VISIBLE);
        }

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
                txtAge.setText(getString(R.string.age_not_available));
            }
        }

        if (u.location != null) {
            Float distanceBetweenUsers = getDistanceToUser(mUser);
            if (distanceBetweenUsers != null) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
                Boolean usesImperial = sharedPreferences.getBoolean("switchUnit", false);
                if (usesImperial) {
                    String distanceString = String.format("%s %s", (int) Math.ceil(kilometersToMiles(distanceBetweenUsers/1000)), getString(R.string.mi_distance));
                    txtDistance.setText(distanceString);
                } else {
                    String distanceString = String.format("%s %s", (int) Math.ceil(distanceBetweenUsers/1000), getString(R.string.km_distance));
                    txtDistance.setText(distanceString);
                }
            } else {
                txtDistance.setText(R.string.no_distance_available);
            }
        } else {
            txtDistance.setText(R.string.no_distance_available);
        }

        if (u.imgURL == null) {
            Picasso.with(getActivity()).load(R.drawable.ic_profile_picture_big).into(ivUserProfileImage);
        } else {
            if (!u.imgURL.equals("")) {
                Picasso.with(getActivity()).load(u.imgURL).into(ivUserProfileImage);
            }
        }
    }

    private Float getDistanceToUser(User u) {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String locationProvider = locationManager.getBestProvider(criteria, false);
        Boolean hasLocationPermission = this.hasLocationPermission();
        if (hasLocationPermission && locationProvider != null) {
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
    // Converts miles into whole kilometers for consistent search range storage
    private int kilometersToMiles(double miles) {
        return (int) Math.round(miles / 1.609344);
    }

    public Boolean hasLocationPermission() {
        return !(ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }
}
