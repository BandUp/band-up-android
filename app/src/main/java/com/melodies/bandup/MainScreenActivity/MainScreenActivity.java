package com.melodies.bandup.MainScreenActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.ads.*;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.Login;
import com.melodies.bandup.R;
import com.melodies.bandup.UserDetailsActivity;
import com.melodies.bandup.VolleySingleton;
import com.melodies.bandup.helper_classes.User;

import org.json.JSONObject;

public class MainScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        UserListFragment.OnFragmentInteractionListener,
        MatchesFragment.OnListFragmentInteractionListener,
        SettingsFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        PrivacyFragment.OnFragmentInteractionListener,
        ProfileFragment.OnFragmentInteractionListener{

    UserListFragment userListFragment;
    MatchesFragment matchesFragment;
    SettingsFragment settingsFragment;
    AboutFragment aboutFragment;
    PrivacyFragment privacyFragment;
    ProfileFragment profileFragment;

    ProgressDialog logoutDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        profileFragment.onImageSelectResult(requestCode, resultCode, data);
        profileFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        userListFragment = new UserListFragment();
        matchesFragment = new MatchesFragment();
        settingsFragment = new SettingsFragment();
        aboutFragment = new AboutFragment();
        privacyFragment = new PrivacyFragment();
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
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = (AdView)findViewById(R.id.adView);
        AdRequest mAdRequest = new AdRequest.Builder().build();
//        mAdview.loadAd(mAdRequest);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        } else if (id == R.id.nav_about) {
            ft.replace(R.id.mainFrame, aboutFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_about));
        } else if (id == R.id.nav_privacy) {
            ft.replace(R.id.mainFrame, privacyFragment);
            ft.commit();
            setTitle(getString(R.string.main_title_privacy));
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


    public void onClickNextUser(View view) {
        userListFragment.onClickNextUser(view);
    }

    public void onClickPreviousUser(View view) {
        userListFragment.onClickPreviousUser(view);
    }

    public void onClickLike(View view) {
        userListFragment.onClickLike(view);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    @Override
    public void onListFragmentInteraction(User user) {
        matchesFragment.onClickChat(user.id);
    }

    public void onClickDisplayModal(View view) {
        profileFragment.onClickDisplayModal(view);
    }

    public void onClickAboutMe(View view) {
        profileFragment.onClickAboutMe(view);
    }

    public void onClickDetails(View view) { Intent intent = new Intent(MainScreenActivity.this, UserDetailsActivity.class); startActivity(intent); }
}