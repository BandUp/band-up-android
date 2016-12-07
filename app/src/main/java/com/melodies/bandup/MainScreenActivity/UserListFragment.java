package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.MainScreenActivity.adapters.UserListAdapter;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.helper_classes.UserLocation;
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

    // prevents reloading of data from server if used as search results
    private boolean mIsSearch = false;

    private OnFragmentInteractionListener mListener;
    private boolean isSwipeRefresh = false;
    private int currentUserIndex;
    private String userIdBeforeRefresh;

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
    private LinearLayout networkErrorBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;

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
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        networkErrorBar.setVisibility(View.INVISIBLE);
        txtNoUsers.setVisibility(View.INVISIBLE);

        DatabaseSingleton.getInstance(getActivity().getApplicationContext()).getBandUpDatabase().getUserList(new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {

                mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.bandUpGreen));
                progressBar.setVisibility(View.INVISIBLE);
                JSONArray responseArr = null;

                if (response instanceof JSONArray) {
                    responseArr = (JSONArray) response;
                } else {
                    txtNoUsers.setText(getString(R.string.user_list_no_users));
                    txtNoUsers.setVisibility(View.VISIBLE);
                    return;
                }

                if (responseArr.length() == 0) {
                    txtNoUsers.setText(getString(R.string.user_list_no_users));
                    txtNoUsers.setVisibility(View.VISIBLE);
                    return;
                }

                // TODO: Check if not 304.
                mAdapter.clear();
                int minAge = loadUserCredentials("minAge");
                int maxAge = loadUserCredentials("maxAge");

                if (isSwipeRefresh) {
                    mAdapter = new UserListAdapter(getChildFragmentManager());
                    mPager.setAdapter(mAdapter);
                }

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
                            Integer age = user.ageCalc();
                            UserLocation userLocation = new UserLocation();
                            if (!item.isNull("location")) {

                                JSONObject location = item.getJSONObject("location");
                                if (!location.isNull("lat")) {
                                    userLocation.setLatitude(location.getDouble("lat"));
                                }

                                if (!location.isNull("lon")) {
                                    userLocation.setLongitude(location.getDouble("lon"));
                                }

                                if (!location.isNull("valid")) {
                                    userLocation.setValid(location.getBoolean("valid"));
                                }
                            } else {
                                userLocation.setValid(false);
                            }
                            user.location = userLocation;
                            if (settingsAgeFilter(age, minAge, maxAge)) {
                                mAdapter.addUser(user);
                            }
                        }

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), R.string.matches_error_json, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    } catch (ParseException e) {
                        Toast.makeText(getActivity(), R.string.matches_error_date, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

                mAdapter.notifyDataSetChanged();
                if (isSwipeRefresh) {
                    mPager.setCurrentItem(mAdapter.getPositionById(userIdBeforeRefresh));
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
                if (mAdapter.getCount() == 0) {
                    txtNoUsers.setText(getString(R.string.user_list_no_users));
                    txtNoUsers.setVisibility(View.VISIBLE);
                    return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
                isSwipeRefresh = false;

            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {

                progressBar.setVisibility(View.INVISIBLE);
                txtNoUsers.setText(R.string.user_list_error_fetch_list);
                txtNoUsers.setVisibility(View.VISIBLE);

                if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    networkErrorBar.setVisibility(View.VISIBLE);
                    networkErrorBar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            networkErrorBar.setVisibility(View.INVISIBLE);
                            getUserList();
                        }
                    });
                    return;
                }
                VolleySingleton.getInstance(getActivity()).checkCauseOfError(error);
                mSwipeRefreshLayout.setRefreshing(false);
                isSwipeRefresh = false;
            }
        });
    }

    // Loading user credentials
    public Integer loadUserCredentials(String valueName) {
        if (getActivity() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SettingsFileAge", Context.MODE_PRIVATE);
            return sharedPreferences.getInt (valueName, 13);
        }
        return -1;
    }

    // displaying only chosen one
    private boolean settingsAgeFilter(Integer age, int minAge, int maxAge) {
        if (age == null) {
            return true;
        }
        return (age >= minAge && age <= maxAge);
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
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // TODO: Do not clear, but replace the items that are different.
                isSwipeRefresh = true;
                // currentUserIndex is changed in the OnPageChangeListener
                userIdBeforeRefresh = mAdapter.getUser(currentUserIndex).id;
                getUserList();
            }
        });

        mPager.addOnPageChangeListener( new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled( int position, float v, int i1 ) {
            }

            @Override
            public void onPageSelected( int position ) {
                currentUserIndex = position;
            }

            @Override
            public void onPageScrollStateChanged( int state ) {
                mSwipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });




        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.bandUpGreen));

        networkErrorBar = (LinearLayout) getActivity().findViewById(R.id.network_connection_error_bar);

        partialView.setVisibility(View.VISIBLE);
        if (mAdapter.getCount() == 0) {
            if (networkErrorBar.getVisibility() == View.INVISIBLE) {
                partialView.setVisibility(View.INVISIBLE);
                getUserList();
            } else {
                txtNoUsers.setText(getString(R.string.user_list_error_fetch_list));
                txtNoUsers.setVisibility(View.VISIBLE);
            }

            if (mIsSearch) {
                txtNoUsers.setText(R.string.search_no_results);
                txtNoUsers.setVisibility(View.VISIBLE);
            }

        } else {
            partialView.setVisibility(View.VISIBLE);
        }

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