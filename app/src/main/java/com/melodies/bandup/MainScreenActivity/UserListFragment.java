package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

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

    private TextView txtName, txtStatus, txtDistance, txtPercentage, txtInstruments, txtGenres;
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
        String url = getResources().getString(R.string.api_address).concat("/nearby-users");
        JsonArrayRequest jsonInstrumentRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new JSONArray(),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject item = response.getJSONObject(i);
                                UserListController.User user = new UserListController.User();
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleySingleton.getInstance(getActivity()).checkCauseOfError(getActivity(), error);

                    }
                }
        );

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(jsonInstrumentRequest);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);
        txtName        = (TextView) rootView.findViewById(R.id.txtName);
        txtStatus      = (TextView) rootView.findViewById(R.id.txtStatus);
        txtDistance    = (TextView) rootView.findViewById(R.id.txtDistance);
        txtPercentage  = (TextView) rootView.findViewById(R.id.txtPercentage);
        txtInstruments = (TextView) rootView.findViewById(R.id.txtInstruments);
        txtGenres      = (TextView) rootView.findViewById(R.id.txtGenres);
        partialView        = rootView.findViewById(R.id.user_partial_view);
        ivUserProfileImage = (ImageView) rootView.findViewById(R.id.imgProfile);

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

    private void displayUser(UserListController.User u) {
        txtName.setText(u.name);
        txtStatus.setText(u.status);
        txtDistance.setText(u.distance+" km.");
        txtPercentage.setText(u.percentage+"%");

        txtInstruments.setText("");
        for (int i = 0; i < u.instruments.size(); i++) {
            txtInstruments.append(u.instruments.get(i)+" ");
        }

        txtGenres.setText("");
        for (int i = 0; i < u.genres.size(); i++) {
            txtGenres.append(u.genres.get(i)+" ");
        }

        ImageLoader il = VolleySingleton.getInstance(getActivity()).getImageLoader();
        ivUserProfileImage.setImageResource(R.color.transparent);
        if (u.imgURL != null && !u.imgURL.equals("")) {
            il.get(u.imgURL, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    final Bitmap b = response.getBitmap();
                    if (b != null) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                ivUserProfileImage.setImageBitmap(b);
                            }
                        };
                        getActivity().runOnUiThread(r);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleySingleton.getInstance(getActivity()).checkCauseOfError(getActivity(), error);
                }
            });
        }
    }

    public void onClickNextUser(View view) {
        UserListController.User u = ulc.getNextUser();
        if (u == null) {
            return;
        }
        displayUser(u);
    }

    public void onClickPreviousUser(View view) {
        UserListController.User u = ulc.getPrevUser();
        if (u == null) {
            return;
        }
        displayUser(u);
    }

}