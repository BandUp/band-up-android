package com.melodies.bandup.MainScreenActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.android.gms.common.api.GoogleApiClient;
import com.melodies.bandup.BuildConfig;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.Login;
import com.melodies.bandup.R;
import com.melodies.bandup.SoundCloudFragments.SoundCloudLoginFragment;
import com.melodies.bandup.SoundCloudFragments.SoundCloudPlayerFragment;
import com.melodies.bandup.SoundCloudFragments.SoundCloudSelectorFragment;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.gcm_tools.RegistrationIntentService;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import static android.os.Build.VERSION_CODES.M;
import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;


public class MainScreenActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        UserListFragment.OnFragmentInteractionListener,
        MatchesFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        UserDetailsFragment.OnFragmentInteractionListener,
        SoundCloudSelectorFragment.OnFragmentInteractionListener,
        SoundCloudLoginFragment.OnFragmentInteractionListener,
        SoundCloudPlayerFragment.OnFragmentInteractionListener,
        UserSearchFragment.OnFragmentInteractionListener,
        UpcomingFeaturesFragment.OnFragmentInteractionListener,
        LocationListener {

    final public int NEAR_ME_FRAGMENT      = 0;
    final public int MY_PROFILE_FRAGMENT   = 1;
    final public int MATCHES_FRAGMENT      = 2;
    final public int SETTINGS_FRAGMENT     = 3;
    final public int COMING_SOON_FRAGMENT  = 4;
    final public int SEARCH_FRAGMENT       = 5;
    final public int USER_DETAILS_FRAGMENT = 6;

    public int currentFragment = NEAR_ME_FRAGMENT;

    private static final int EDIT_PROFILE_REQUEST_CODE = 3929;

    UserListFragment userListFragment;
    UserListFragment mUserSearchResultsFragment;
    UserDetailsFragment userDetailsFragment;
    MatchesFragment matchesFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;
    UserSearchFragment mUserSearchFragment;
    UpcomingFeaturesFragment mUpcomingFeaturesFragment;

    ProgressDialog logoutDialog;
    LocationManager locationManager;
    Criteria criteria;
    String bestProvider;
    SharedPreferences sharedPrefs;
    GoogleApiClient mGoogleApiClient;
    User currentUser;

    ImageView imgProfileNav;
    TextView txtUsernameNav;
    TextView txtFavoriteNav;

    NavigationView navigationView;

    private LinearLayout networkErrorBar;

    // prevents reloading of data from server if used as search results
    private boolean mIsSearch = false;

    public void setIsSearch(boolean isSearch){
        mIsSearch = isSearch;
    }

    public Boolean getIsSearch(){
        return mIsSearch;
    }

    public Boolean hasLocationPermission() {
        return !(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        profileFragment.onImageSelectResult(requestCode, resultCode, data);
        profileFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (currentFragment == MY_PROFILE_FRAGMENT) {
            getMenuInflater().inflate(R.menu.menu_profile, menu);
            MenuItem item = menu.findItem(R.id.action_edit_profile);
            item.getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

            if (currentFragment != MY_PROFILE_FRAGMENT) {
                item.setVisible(false);
            }

        } else {
            getMenuInflater().inflate(R.menu.menu_search, menu);
            MenuItem item = menu.findItem(R.id.action_search);
            item.getIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
            if (currentFragment != NEAR_ME_FRAGMENT || mIsSearch) {
                item.setVisible(false);
            }
        }


        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()) {
            case R.id.action_search:
                if (currentFragment == NEAR_ME_FRAGMENT) {
                    FragmentManager fm = getSupportFragmentManager();
                    ft = fm.beginTransaction().replace(R.id.mainFrame, mUserSearchFragment, "userSearchFragment").addToBackStack(null);
                    ft.commit();
                    setTitle(getString(R.string.search));
                    currentFragment = SEARCH_FRAGMENT;
                    invalidateOptionsMenu();
                }
                break;
            case R.id.action_edit_profile:
                Intent aboutMeIntent = new Intent(MainScreenActivity.this, UpdateAboutMe.class);
                Bundle asdf = new Bundle();
                asdf.putString("USER_ID", currentUser.id);
                asdf.putString("USER_NAME", currentUser.name);
                asdf.putSerializable("USER_DATE_OF_BIRTH", currentUser.dateOfBirth);
                asdf.putString("USER_FAVOURITE_INSTRUMENT", currentUser.favoriteinstrument);
                asdf.putStringArrayList("USER_INSTRUMENTS", (ArrayList<String>) currentUser.instruments);
                asdf.putStringArrayList("USER_GENRES", (ArrayList<String>) currentUser.genres);
                asdf.putString("USER_ABOUT_ME", currentUser.aboutme);

                aboutMeIntent.putExtras(asdf);

                startActivityForResult(aboutMeIntent, EDIT_PROFILE_REQUEST_CODE);
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onClickDeleteAccount(View view) {
        settingsFragment.onClickDeleteAccount(view);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(networkStateReceiver);
        super.onDestroy();
    }
    BroadcastReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = new User();
        getUserProfile();
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        bestProvider = locationManager.getBestProvider(criteria, false);
        setContentView(R.layout.activity_main_screen);
        sharedPrefs = getSharedPreferences("permissions", Context.MODE_PRIVATE);
        if (!sharedPrefs.contains("display_rationale")) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean("display_rationale", true);
            editor.apply();
        }

         networkStateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                    networkErrorBar.setVisibility(View.VISIBLE);
                } else {
                    networkErrorBar.setVisibility(View.INVISIBLE);
                }
            }
        };

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateReceiver, filter);

        // Create all fragments
        userListFragment            = UserListFragment.newInstance(null);
        userDetailsFragment         = UserDetailsFragment.newInstance();
        matchesFragment             = new MatchesFragment();
        settingsFragment            = SettingsFragment.newInstance();
        profileFragment             = ProfileFragment.newInstance();
        mUserSearchFragment         = UserSearchFragment.newInstance();
        mUpcomingFeaturesFragment   = UpcomingFeaturesFragment.newInstance();

        networkErrorBar = (LinearLayout) findViewById(R.id.network_connection_error_bar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Set the first item in the drawer to selected.
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);


        // Open the UserListFragment
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, userListFragment);
        ft.commit();

        // We know the user is logged in time to start services
        startService(new Intent(getApplicationContext(), RegistrationIntentService.class));
        //startService(new Intent(getApplicationContext(), BandUpGCMListenerService.class));
        Boolean shouldDisplayRationale = sharedPrefs.getBoolean("display_rationale", false);

        int apiVersion = android.os.Build.VERSION.SDK_INT;
        if (apiVersion >= M){

            if (!hasLocationPermission()) {
                // If location permissions have NOT been granted.
                // Tell the user what we are going to do with the location.
                if (shouldDisplayRationale) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainScreenActivity.this);
                    builder.setTitle("Location Access")
                            .setMessage(R.string.location_rationale)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainScreenActivity.this, new String[]{
                                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                                    }, LOCATION_REQUEST_CODE);
                                }
                            })
                            .show();
                }
            } else {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("display_rationale", true);
                editor.apply();
                createLocationRequest();
            }

        } else {
            createLocationRequest();
        }
        imgProfileNav  = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.imgProfileNav);
        txtUsernameNav = (TextView)  navigationView.getHeaderView(0).findViewById(R.id.txtUsernameNav);
        txtFavoriteNav = (TextView)  navigationView.getHeaderView(0).findViewById(R.id.txtFavoriteNav);
        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                navigationView.getMenu().getItem(1).setChecked(true);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.mainFrame, profileFragment);
                ft.commit();
                invalidateOptionsMenu();
            }
        });
    }
    boolean isExiting = false;

    public void updateNavUserImage(String url) {
        if (!url.equals("")) {
            Picasso.with(MainScreenActivity.this).load(url).into(imgProfileNav);
        }
    }

    public void updateNavUserName(String name) {
        txtUsernameNav.setText(name);
    }

    public void updateFavouriteInstrument(String instrumentName) {
        txtFavoriteNav.setText(instrumentName);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        FragmentManager fm = getSupportFragmentManager();
        int fragCount = fm.getBackStackEntryCount();
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragCount != 0) {
            fm.popBackStack();
        } else if (currentFragment != NEAR_ME_FRAGMENT) {
            FragmentTransaction ft;
            ft = fm.beginTransaction().replace(R.id.mainFrame, userListFragment, "userListFragment");
            ft.commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        } else {
            if (isExiting) {
                super.onBackPressed();
                return;
            }

            this.isExiting = true;
            Toast.makeText(this, R.string.exit_bandup_toast, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    isExiting = false;
                }
            }, 2000);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        FragmentManager fm = getSupportFragmentManager();
        switch (id){
            case R.id.nav_near_me:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                mIsSearch = false;
                ft.replace(R.id.mainFrame, userListFragment);
                ft.commit();
                break;
            case R.id.nav_matches:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                ft.replace(R.id.mainFrame, matchesFragment);
                ft.commit();
                break;
            case R.id.nav_edit_profile:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                ft.replace(R.id.mainFrame, profileFragment);
                ft.commit();

                break;
            case R.id.nav_settings:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                ft.replace(R.id.mainFrame, settingsFragment);
                ft.commit();
                break;
            case R.id.nav_upcomming:
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
                ft.replace(R.id.mainFrame, mUpcomingFeaturesFragment);
                ft.commit();
                break;
            case R.id.nav_logout:
                logoutDialog = new ProgressDialog(MainScreenActivity.this);
                logoutDialog.setMessage(getString(R.string.main_log_out_message));
                logoutDialog.setTitle(getString(R.string.main_log_out_title));
                logoutDialog.show();
                logout();
            break;
            default:
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        DatabaseSingleton.getInstance(MainScreenActivity.this).getBandUpDatabase().logout(
                new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                logoutDialog.dismiss();
                Intent intent = new Intent(MainScreenActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                logoutDialog.dismiss();
                VolleySingleton.getInstance(MainScreenActivity.this).checkCauseOfError(error);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * Used for the MatchesFragment. When the user taps on another user.
     * @param user The the user that the current user wants to chat with.
     */
    @Override
    public void onListFragmentInteraction(User user) {
        matchesFragment.onClickChat(user);
    }

    public void onClickDisplayModal(View view) {
        profileFragment.onClickDisplayModal(view);
    }

    public void onClickContact(View view) {
        String subject = String.format("Help with Band Up %s (%s)", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@bandup.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onClickPrivacyPolicy(View view) { settingsFragment.onClickPrivacyPolicy(view); }

    public UserListFragment startSearchResults(JSONArray users){
        mIsSearch = true;
        mUserSearchResultsFragment = UserListFragment.newInstance(users);

        setTitle(getString(R.string.main_title_search_results));
        return mUserSearchResultsFragment;
    }

    public void onClickDetails(View view, int position) {
        Bundle bundle = new Bundle();
        if (mIsSearch){
            if (mUserSearchResultsFragment.mAdapter.getUser(position) == null) {
                return;
            }
            bundle.putString("user_id", mUserSearchResultsFragment.mAdapter.getUser(position).id);
        } else {
            if (userListFragment.mAdapter.getUser(position) == null) {
                return;
            }
            bundle.putString("user_id", userListFragment.mAdapter.getUser(position).id);
        }

        if (userDetailsFragment.getArguments() != null) {
            userDetailsFragment.getArguments().clear();
            userDetailsFragment.getArguments().putAll(bundle);
        } else {
            userDetailsFragment.setArguments(bundle);
        }
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction().replace(R.id.mainFrame, userDetailsFragment, "userDetailsFragment").addToBackStack(null);
        ft.commit();
        currentFragment = USER_DETAILS_FRAGMENT;
        invalidateOptionsMenu();
    }

    public void onClickSearch(View view) {
        mUserSearchFragment.onClickSearch(view);
    }

    public void onShowGenres(View v) {
        mUserSearchFragment.onShowGenres(v);
    }

    public void onShowInstruments(View v) {
        mUserSearchFragment.onShowInstruments(v);
    }

    // ======= Location setup ========
    private final int LOCATION_REQUEST_CODE = 333;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, yay! Do the contacts-related task you need to do.

                // We will display the rationale next time we are denied access to the location.
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("display_rationale", true);
                editor.apply();
                createLocationRequest();
            } else {
                // Permission denied, boo!
                // Since the user has denied, we will not display it again.
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("display_rationale", false);
                editor.apply();
                //createLocationRequest();
            }
            return;
        }
        profileFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void createLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
        try {
            Location location = locationManager.getLastKnownLocation(bestProvider);
            // Get a new location every two minutes
            locationManager.requestLocationUpdates(bestProvider, 120000, 0, this);


            sendLocation(location);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }catch (SecurityException ex){
            Toast.makeText(this, "Security Exception: " + ex, Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }

    private void sendLocation(Location location){
        if (location == null){
            System.err.println("Location is null when sending location.");
            System.err.println(Arrays.toString(Thread.currentThread().getStackTrace()));
            return;
        }

        JSONObject locObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            locObject.put("lon", location.getLongitude());
            locObject.put("lat", location.getLatitude());

            jsonObject.put("location", locObject);
            System.out.println(locObject);
            DatabaseSingleton.getInstance(this).getBandUpDatabase().postLocation(jsonObject,
                    new BandUpResponseListener() {
                        @Override
                        public void onBandUpResponse(Object response) {
                            // we were successful nothing to report
                        }
                    }, new BandUpErrorListener() {
                        @Override
                        public void onBandUpErrorResponse(VolleyError error) {
                            error.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Something went wrong sending location", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        sendLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void onClickLike(final User user, final View likeButton) {
        JSONObject jsonUser = new JSONObject();

        try {
            jsonUser.put("userID", user.id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance(MainScreenActivity.this.getApplicationContext()).getBandUpDatabase().postLike(jsonUser, new BandUpResponseListener() {
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
                    Boolean isMatch;
                    if (!responseObj.isNull("isMatch")) {
                        isMatch = responseObj.getBoolean("isMatch");
                    } else {
                        Toast.makeText(MainScreenActivity.this, R.string.main_error_match, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (likeButton instanceof Button) {
                        Button likeBtn = (Button) likeButton;
                        likeBtn.setText(getString(R.string.user_list_liked));
                        likeBtn.setEnabled(false);
                        likeBtn.setBackgroundResource(R.drawable.button_user_list_like_disabled);
                    }
                    user.isLiked = true;
                    userListFragment.mAdapter.likeUserById(user.id);
                    if (isMatch) {
                        Toast.makeText(MainScreenActivity.this, R.string.main_matched, Toast.LENGTH_SHORT).show();
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

                VolleySingleton.getInstance(MainScreenActivity.this).checkCauseOfError(error);

            }
        });
    }

    public void onClickSave(View v){
        System.out.println("FOOOO");
    }

    // Get the User ID of the logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String id = srdPref.getString("userID", DEFAULT);
        return (!id.equals(DEFAULT)) ? id : "No data Found";
    }

    public void getUserProfile() {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        DatabaseSingleton.getInstance(MainScreenActivity.this.getApplicationContext())
                .getBandUpDatabase().getUserProfile(user, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                }
                if (responseObj != null) {
                    // Binding View to real data
                    currentUser = new User(responseObj);
                    txtUsernameNav.setText(currentUser.name);
                    txtFavoriteNav.setText(currentUser.favoriteinstrument);

                    if (currentUser.imgURL != null) {
                        if (!currentUser.imgURL.equals("")) {
                            Picasso.with(MainScreenActivity.this).load(currentUser.imgURL).into(imgProfileNav);
                        }
                    } else {
                        Picasso.with(MainScreenActivity.this).load(R.drawable.ic_profile_picture_placeholder).into(imgProfileNav);

                    }
                }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {

            }
        });
    }
}