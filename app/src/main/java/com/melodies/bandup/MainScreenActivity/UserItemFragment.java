package com.melodies.bandup.MainScreenActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class UserItemFragment extends Fragment {
    int mNum;

    private TextView txtName, txtDistance, txtInstruments, txtGenres, txtPercentage, txtAge;
    private Button btnLike, btnDetails;
    private ImageView ivUserProfileImage;
    private AdView mAdView;
    private User mUser;

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
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLike(v);
            }
        });
        btnDetails  = (Button)    rootView.findViewById(R.id.btnDetails);
        btnDetails.setOnClickListener(new View.OnClickListener() {
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
        populateUser(mUser);

        return rootView;
    }

    /**
     * Displays the user 'u' in the fragment
     * @param u
     */
    private void populateUser(User u) {
        LocaleRules localeRules = LocaleSingleton.getInstance(getActivity()).getLocaleRules();

        txtName.setText(u.name);

        if (u.favoriteinstrument != null) {
            txtInstruments.setText(u.favoriteinstrument);
        }
        else {
            txtInstruments.setText(u.instruments.get(0));
        }

        if (u.genres.size() > 0) {
            txtGenres.setText(u.genres.get(0));
        }

        txtPercentage.setText(u.percentage + "%");

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

        if (u.distance != null) {
            String distanceString = String.format("%s %s", u.distance, getString(R.string.km_distance));
            txtDistance.setText(distanceString);
        } else {
            txtDistance.setText(R.string.no_distance_available);
        }

        if (u.imgURL == null) {
            Picasso.with(getActivity()).load(R.drawable.ic_profile_picture_big).into(ivUserProfileImage);
        } else {
            Picasso.with(getActivity()).load(u.imgURL).into(ivUserProfileImage);

        }
    }

    public void onClickLike(View view) {
        JSONObject user = new JSONObject();

        try {
            user.put("userID", mUser.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance(getActivity().getApplicationContext()).getBandUpDatabase().postLike(user, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;

                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                } else {
                    return;
                }

                try {
                    Boolean isMatch;
                    if (!responseObj.isNull("isMatch")) {
                        isMatch = responseObj.getBoolean("isMatch");
                    } else {
                        Toast.makeText(getActivity(), "Error loading match.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (isMatch) {
                        Toast.makeText(getActivity(), "You Matched!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "You liked this person", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ViewPager pager = ((UserListFragment)getParentFragment()).mPager;
                pager.setCurrentItem(pager.getCurrentItem() + 1, true);
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);

            }
        });
    }
}
