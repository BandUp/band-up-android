package com.melodies.bandup.MainScreenActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bergthor on 1.11.2016.
 */

public class UserArrayListFragment extends Fragment {
    int mNum;

    private TextView txtName, txtDistance, txtInstruments, txtGenres, txtPercentage, txtAge, txtNoUsers;
    private ProgressBar progressBar;
    private Button btnLike, btnDetails;
    private View     partialView;
    private ImageView ivUserProfileImage;

    private User mUser;

    /**
     * Create a new instance of CountingFragment, providing "num"
     * as an argument.
     */
    static UserArrayListFragment newInstance(int num, User user) {
        UserArrayListFragment f = new UserArrayListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        args.putSerializable("user", user);
        f.setArguments(args);

        return f;
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        txtNoUsers         = (TextView)  rootView.findViewById(R.id.txtNoUsers);
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

                System.out.println(mNum);
                ((MainScreenActivity) getActivity()).onClickDetails(v, mNum);
            }
        });
    }

    private void setFonts() {
        txtName       .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtInstruments.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtGenres     .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtDistance   .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtPercentage .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));
        txtAge        .setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/caviar_dreams.ttf"));

        btnLike.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/master_of_break.ttf"));
        btnDetails.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/master_of_break.ttf"));
    }


    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_list_user_partial, container, false);

        initializeTextViews(rootView);
        initializeButtons(rootView);
        setFonts();

        displayUser(mUser);
        return rootView;
    }

    private void displayUser(User u) {
        txtName.setText(u.name);
        // Getting the first item for now.
        txtInstruments.setText(u.instruments.get(0));
        txtGenres.setText(u.genres.get(0));
        txtPercentage.setText(u.percentage + "%");
        if (u.age == 1) {
            txtAge.setText(u.age + " year old");
        } else {
            txtAge.setText(u.age + " years old");
        }

        if (u.distance != null) {
            txtDistance.setText(u.distance + " km away from you");
        } else {
            txtDistance.setText("-- km away from you");
        }

        /*for (int i = 0; i < u.instruments.size(); i++) {
            if (i != u.instruments.size()-1) {
                txtInstruments.append(u.instruments.get(i) + ", ");
            } else {
                txtInstruments.append(u.instruments.get(i));
            }
        }*/

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
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);

            }
        });
    }
}
