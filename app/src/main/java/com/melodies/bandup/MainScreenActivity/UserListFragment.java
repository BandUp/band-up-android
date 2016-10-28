package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public UserListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserListFragment newInstance(String param1, String param2) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private TextView txtName, txtDistance, txtInstruments, txtGenres, txtPercentage, txtAge;
    private Button btnLike, btnDetails;
    private View     partialView;
    private ImageView ivUserProfileImage;
    UserListController ulc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        ulc = new UserListController();

        DatabaseSingleton.getInstance(getActivity().getApplicationContext()).getBandUpDatabase().getUserList(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONArray responseArr = null;

                if (response instanceof JSONArray) {
                    responseArr = (JSONArray) response;
                } else {
                    return;
                }

                for (int i = 0; i < responseArr.length(); i++) {
                    try {
                        JSONObject item = responseArr.getJSONObject(i);
                        User user = new User();
                        if (!item.isNull("_id"))      user.id = item.getString("_id");
                        if (!item.isNull("username")) user.name = item.getString("username");
                        if (!item.isNull("status"))   user.status = item.getString("status");
                        if (!item.isNull("distance")) user.distance = item.getInt("distance");

                        user.percentage = item.getInt("percentage");
                        if(!item.isNull("image")) {
                            JSONObject userImg = item.getJSONObject("image");
                            if (!userImg.isNull("url")) {
                                user.imgURL = userImg.getString("url");
                            }
                        }

                        JSONArray instrumentArray = item.getJSONArray("instruments");

                        for (int j = 0; j < instrumentArray.length(); j++) {
                            user.instruments.add(instrumentArray.getString(j));
                        }

                        JSONArray genreArray = item.getJSONArray("genres");

                        for (int j = 0; j < genreArray.length(); j++) {
                            user.genres.add(genreArray.getString(j));
                        }
                        ulc.addUser(user);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                partialView.setVisibility(partialView.VISIBLE);
                if (ulc.users.size() > 0) {
                    displayUser(ulc.getUser(0));
                }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);
            }
        });
    }

    private void initializeTextViews(View rootView) {
        ivUserProfileImage = (ImageView) rootView.findViewById(R.id.imgProfile);
        txtName            = (TextView)  rootView.findViewById(R.id.txtName);
        txtInstruments     = (TextView)  rootView.findViewById(R.id.txtMainInstrument);
        txtGenres          = (TextView)  rootView.findViewById(R.id.txtGenres);
        txtDistance        = (TextView)  rootView.findViewById(R.id.txtDistance);
        txtPercentage      = (TextView)  rootView.findViewById(R.id.txtPercentage);
        txtAge             = (TextView)  rootView.findViewById(R.id.txtAge);
    }

    private void initializeButtons(View rootView) {
        btnLike     = (Button)    rootView.findViewById(R.id.btnLike);
        btnDetails  = (Button)    rootView.findViewById(R.id.btnDetails);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);

        initializeTextViews(rootView);
        initializeButtons(rootView);
        partialView = rootView.findViewById(R.id.user_partial_view);

        setFonts();

        if (ulc.getCurrentUser() != null){
            displayUser(ulc.getCurrentUser());
            partialView.setVisibility(partialView.VISIBLE);
        }

        return rootView;
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

    public void onClickLike(View view) {
        JSONObject user = new JSONObject();

        try {
            user.put("userID", ulc.getCurrentUser().id);
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

    public User getCurrentUser() {
        return ulc.getCurrentUser();
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

    public void onClickNextUser(View view) {
        User u = ulc.getNextUser();
        if (u == null) {
            return;
        }
        displayUser(u);
    }

    public void onClickPreviousUser(View view) {
        User u = ulc.getPrevUser();
        if (u == null) {
            return;
        }
        displayUser(u);
    }
}