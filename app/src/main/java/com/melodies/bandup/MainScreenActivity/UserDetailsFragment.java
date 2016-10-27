package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.melodies.bandup.R.id.imgProfile;

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

    private TextView txtName;
    private TextView txtInstruments;
    private TextView txtGenres;
    private TextView txtAge;
    private TextView txtFavorite;
    private TextView txtPercentage;
    private TextView txtAboutMe;
    private ListView lstInstruments;
    private ListView lstGenres;
    private ImageView ivUserProfileImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Gets the user_id from userListFragment
        userCurrentUser(getArguments().getString("user_id"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        txtName            = (TextView)  rootView.findViewById(R.id.txtName);
        txtInstruments     = (TextView)  rootView.findViewById(R.id.txtInstruments);
        txtGenres          = (TextView)  rootView.findViewById(R.id.txtGenres);
        txtAge             = (TextView)  rootView.findViewById(R.id.txtAge);
        txtFavorite        = (TextView)  rootView.findViewById(R.id.txtFavorite);
        txtPercentage      = (TextView)  rootView.findViewById(R.id.txtPercentage);
        txtAboutMe         = (TextView)  rootView.findViewById(R.id.txtAboutMe);
        ivUserProfileImage = (ImageView) rootView.findViewById(imgProfile);
        lstInstruments     = (ListView)  rootView.findViewById(R.id.lstInstruments);
        lstGenres          = (ListView)  rootView.findViewById(R.id.lstGenres);
        return rootView;
    }

    // Request REAL user info from server
    public void userCurrentUser(String userid) {
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
                    try {
                        if (!responseObj.isNull("username")) {
                            txtName.setText(responseObj.getString("username"));
                        }
                        if (!responseObj.isNull("age")) {
                            txtAge.setText(String.format("%s%s", responseObj.getString("age"), " years old"));
                        }

                        txtFavorite.setText("Drums");

                        txtPercentage.setText("45%");

                        if (!responseObj.isNull("genres")) {
                            txtGenres.setText(responseObj.getString("genres"));
                        }
                        if (!responseObj.isNull("instruments")) {
                            txtInstruments.setText(responseObj.getString("instruments"));
                        }
                        if (!responseObj.isNull("aboutme")) {
                            txtAboutMe.setText(responseObj.getString("aboutme"));
                        }
                        if (!responseObj.isNull("image")) {
                            JSONObject imageObj = responseObj.getJSONObject("image");
                            if (!imageObj.isNull("url")) {
                                Picasso.with(getActivity()).load(imageObj.getString("url")).into(ivUserProfileImage);
                            }
                        } else {
                            // TODO: Could not get image, display the placeholder.
                        }

                    } catch (JSONException e) {
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
