package com.melodies.bandup.MainScreenActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.Login;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.gcm_tools.RegistrationIntentService;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Genres;
import com.melodies.bandup.setup.Instruments;

import org.json.JSONException;
import org.json.JSONObject;

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserListFragment.OnFragmentInteractionListener,
        MatchesFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener,
        UserDetailsFragment.OnFragmentInteractionListener,
        LocationListener{

    UserListFragment userListFragment;
    UserDetailsFragment userDetailsFragment;
    MatchesFragment matchesFragment;
    SettingsFragment settingsFragment;
    ProfileFragment profileFragment;

    ProgressDialog logoutDialog;
    LocationManager locationManager;
    Criteria criteria;
    String bestProvider;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        profileFragment.onImageSelectResult(requestCode, resultCode, data);
        profileFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == LOCATION_REQUEST_CODE){
           //createLocationRequest();
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the contacts-related task you need to do.
                //createLocationRequest();
            } else {
                // permission denied, boo!
                Toast.makeText(this, R.string.user_allow_location, Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
               // createLocationRequest();
            }
            return;
        }
        profileFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userListFragment = new UserListFragment();
        userDetailsFragment = new UserDetailsFragment();
        matchesFragment = new MatchesFragment();
        settingsFragment = new SettingsFragment();
        profileFragment = new ProfileFragment();
        logoutDialog = new ProgressDialog(MainScreenActivity.this);
        logoutDialog.setMessage("Logging out");
        logoutDialog.setTitle("Please wait...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame, userListFragment);
        ft.commit();

        // we know user is logged in time to start services
        startService(new Intent(getApplicationContext(), RegistrationIntentService.class));
        //startService(new Intent(getApplicationContext(), BandUpGCMListenerService.class));

//        createLocationRequest();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (count != 0){
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (id == R.id.nav_near_me) {
            ft.replace(R.id.mainFrame, userListFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_user_list));
        } else if (id == R.id.nav_matches) {
            ft.replace(R.id.mainFrame, matchesFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_matches));
        } else if (id == R.id.nav_edit_profile) {
            ft.replace(R.id.mainFrame, profileFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_edit_profile));
        } else if (id == R.id.nav_settings) {
            ft.replace(R.id.mainFrame, settingsFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_settings));
        } else if (id == R.id.nav_logout) {
            logout();
            logoutDialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void logout() {
        String url = getResources().getString(R.string.api_address).concat("/logout");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        logoutDialog.dismiss();
                        Intent intent = new Intent(MainScreenActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        logoutDialog.dismiss();
                        VolleySingleton.getInstance(MainScreenActivity.this).checkCauseOfError(error);
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(User user) {
        matchesFragment.onClickChat(user);
    }

    public void onClickDisplayModal(View view) {
        profileFragment.onClickDisplayModal(view);
    }

    public void onClickAboutMe(View view) {
        profileFragment.onClickAboutMe(view);
    }

    public void onClickDetails(View view, int position) {
        System.out.println(position);
        switch (view.getId()) {
            case R.id.btnDetails:
                Bundle bundle = new Bundle();
                if (userListFragment.mAdapter.getUser(position) == null) {
                    return;
                }
                bundle.putString("user_id", userListFragment.mAdapter.getUser(position).id);
                System.out.println(userDetailsFragment.getArguments());

                if (userDetailsFragment.getArguments() != null) {
                    userDetailsFragment.getArguments().clear();
                    userDetailsFragment.getArguments().putAll(bundle);
                } else {
                    userDetailsFragment.setArguments(bundle);
                }
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().replace(R.id.mainFrame, userDetailsFragment).addToBackStack(null);
                ft.commit();
                break;
        }
    }

    // ======= Location setup ========
    private final int LOCATION_REQUEST_CODE = 333;
/*
    protected void createLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);

            return;
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        try{
            Location location = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            if (location == null) {
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
            //sendLocation(location);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }
    */
    private void sendLocation(Location location){
        JSONObject locObject = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            locObject.put("lon", location.getLongitude());
            locObject.put("lat", location.getLatitude());

            jsonObject.put("location", locObject);

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
                            Toast.makeText(getApplicationContext(), "something went wrong sending location", Toast.LENGTH_LONG).show();
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

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

    public void onClickEditInstruments(View view) {
        startActivity(new Intent(MainScreenActivity.this, Instruments.class));
    }

    public void onClickEditGenres(View view) {
        startActivity(new Intent(MainScreenActivity.this, Genres.class));
    }
}