package com.melodies.bandup.MainScreenActivity;

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

import com.melodies.bandup.MainScreenActivity.dummy.DummyContent;
import com.melodies.bandup.R;
import com.melodies.bandup.UserProfile;

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
            Intent toUserProfileIntent = new Intent(MainScreenActivity.this, UserProfile.class);
            MainScreenActivity.this.startActivity(toUserProfileIntent);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void onClickNextUser(View view) {
        userListFragment.onClickNextUser(view);
    }

    public void onClickPreviousUser(View view) {
        userListFragment.onClickPreviousUser(view);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {


    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
}