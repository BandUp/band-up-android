package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;
import com.melodies.bandup.LocaleSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.locale.LocaleRules;
import com.squareup.picasso.Picasso;

public class UserItemFragment extends Fragment {
    int mNum;

    private TextView txtName, txtDistance, txtInstruments, txtGenres, txtPercentage, txtAge;
    private Button btnLike, btnDetails;
    private ImageView ivUserProfileImage;
    private AdView mAdView;
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
                        ((MainScreenActivity)getActivity()).onClickLike(mUser.id);
                        ViewPager pager = ((UserListFragment)getParentFragment()).mPager;
                        if (pager.getCurrentItem() != pager.getAdapter().getCount() - 1) {
                            pager.setCurrentItem(pager.getCurrentItem() + 1, true);
                        }
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

        if (u.distance != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileSwitch", Context.MODE_PRIVATE);
            Boolean usesImperial = sharedPreferences.getBoolean("switchUnit", false);
            if (usesImperial) {
                String distanceString = String.format("%s %s", kilometersToMiles(u.distance), getString(R.string.mi_distance));
                txtDistance.setText(distanceString);
            } else {
                String distanceString = String.format("%s %s", u.distance, getString(R.string.km_distance));
                txtDistance.setText(distanceString);
            }
        } else {
            txtDistance.setText(R.string.no_distance_available);
        }

        if (u.imgURL == null) {
            Picasso.with(getActivity()).load(R.drawable.ic_profile_picture_big).into(ivUserProfileImage);
        } else {
            Picasso.with(getActivity()).load(u.imgURL).into(ivUserProfileImage);

        }
    }
    // Converts miles into whole kilometers for consistent search range storage
    private int kilometersToMiles(int miles) {
        double kilometers = miles / 1.609344;
        return (int) Math.ceil(kilometers);
    }
}
