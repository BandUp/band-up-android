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
import java.util.ArrayList;
import java.util.Collections;
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
    private AdView mAdView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    // prevents reloading of data from server if used as search results
    private boolean mIsSearch = false;

    private OnFragmentInteractionListener mListener;

    public UserListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userlist a list of users to display
     * @return A new instance of fragment UserListFragment.
     */
    public static UserListFragment newInstance(User[] userlist) {
        UserListFragment fragment = new UserListFragment();
        Bundle args = new Bundle();
        if (userlist != null){
            args.putSerializable("userlist", userlist);
        }
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
            if(getArguments().getSerializable("userlist") != null){
                ArrayList<User> userArrayList = new ArrayList<>();
                Collections.addAll(userArrayList, (User[]) getArguments().getSerializable("userlist"));
                mAdapter = new UserListAdapter(getChildFragmentManager());
                mAdapter.clear();
                for (User u : userArrayList){
                    mAdapter.addUser(u);
                }
                mIsSearch = true;
                return;
            }
        }
        mAdapter = new UserListAdapter(getChildFragmentManager());
    }

    /**
     * get users from backend and insert them into the user list adapter
     */
    private void getUserList() {
        if (mIsSearch){
            progressBar.setVisibility(View.INVISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }
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
                        if (responseArr.get(i) != null) {
                            JSONObject item = responseArr.getJSONObject(i);
                            User user = new User();
                            if (!item.isNull("_id")) user.id = item.getString("_id");
                            if (!item.isNull("username")) user.name = item.getString("username");
                            if (!item.isNull("status")) user.status = item.getString("status");
                            if (!item.isNull("distance")) user.distance = item.getInt("distance");

                            user.percentage = item.getInt("percentage");
                            if (!item.isNull("image")) {
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
                        }
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