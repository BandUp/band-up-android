package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.MainScreenActivity.adapters.UserListAdapter;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

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
    private AdView mAdView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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

    private TextView txtNoUsers;
    private ProgressBar progressBar;
    private View     partialView;

    UserListAdapter mAdapter;

    ViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mAdapter = new UserListAdapter(getChildFragmentManager());

    }

    private void getUserList() {
        DatabaseSingleton.getInstance(getActivity().getApplicationContext()).getBandUpDatabase().getUserList(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                progressBar.setVisibility(View.INVISIBLE);
                JSONArray responseArr = null;

                if (response instanceof JSONArray) {
                    responseArr = (JSONArray) response;
                } else {
                    txtNoUsers.setVisibility(View.VISIBLE);
                    return;
                }

                if (responseArr.length() == 0) {
                    txtNoUsers.setVisibility(View.VISIBLE);
                    return;
                }

                // TODO: Check if not 304.
                mAdapter.clear();

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

                        if (!item.isNull("dateOfBirth")) {
                            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            user.dateOfBirth = df.parse(item.getString("dateOfBirth"));
                        }

                        JSONArray instrumentArray = item.getJSONArray("instruments");

                        for (int j = 0; j < instrumentArray.length(); j++) {
                            user.instruments.add(instrumentArray.getString(j));
                        }

                        JSONArray genreArray = item.getJSONArray("genres");

                        for (int j = 0; j < genreArray.length(); j++) {
                            user.genres.add(genreArray.getString(j));
                        }
                        mAdapter.addUser(user);
                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), "Could not parse the Date object.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
                if (partialView != null) {
                    partialView.setVisibility(View.VISIBLE);
                } else {
                    System.err.println("User List partialView is null");
                }

                if (progressBar != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    System.err.println("User List progressBar is null");
                }

                mSwipeRefreshLayout.setRefreshing(false);

            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_list, container, false);

        // Adding ad Banner
        mAdView = (AdView)rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        partialView = rootView.findViewById(R.id.user_linear_layout);
        txtNoUsers  = (TextView) rootView.findViewById(R.id.txtNoUsers);

        progressBar = (ProgressBar) rootView.findViewById(R.id.userListProgressBar);
        progressBar.setVisibility(View.VISIBLE);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: Do not clear, but replace the items that are different.
                getUserList();
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.bandUpGreen));

        partialView.setVisibility(View.VISIBLE);
        getUserList();

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
}