package com.melodies.bandup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.melodies.bandup.UserListController.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UserListController ulc = new UserListController();
    private TextView txtName;
    private TextView txtStatus;
    private TextView txtDistance;
    private TextView txtPercentage;
    private TextView txtInstruments;
    private TextView txtGenres;
    private View     partialView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        txtName        = (TextView) findViewById(R.id.txtName);
        txtStatus      = (TextView) findViewById(R.id.txtStatus);
        txtDistance    = (TextView) findViewById(R.id.txtDistance);
        txtPercentage  = (TextView) findViewById(R.id.txtPercentage);
        txtInstruments = (TextView) findViewById(R.id.txtInstruments);
        txtGenres      = (TextView) findViewById(R.id.txtGenres);
        partialView = findViewById(R.id.user_partial_view);
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
                                User user = new User();
                                user.name = item.getString("username");
                                user.status = item.getString("status");
                                user.distance = item.getInt("distance");
                                user.percentage = item.getInt("percentage");
                                user.imgURL = item.getString("profileImgUrl");

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
                                Toast.makeText(UserList.this, "Could not parse the JSON object.", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                        partialView.setVisibility(partialView.VISIBLE);
                        displayUser(ulc.getUser(0));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserList.this, "Error.", Toast.LENGTH_LONG).show();

                    }
                }
        );

        VolleySingleton.getInstance(UserList.this).addToRequestQueue(jsonInstrumentRequest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

        if (id == R.id.nav_edit_profile) {

        } else if (id == R.id.nav_matches) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

        } else if (id == R.id.nav_edit_profile) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void displayUser(User u) {
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
        final ImageView iv = (ImageView) findViewById(R.id.imgProfile);
        ImageLoader il = VolleySingleton.getInstance(UserList.this).getImageLoader();
        iv.setImageResource(R.color.transparent);
        il.get(u.imgURL, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                final Bitmap b = response.getBitmap();
                if (b != null) {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(b);
                        }
                    };
                    runOnUiThread(r);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR RESPONSE");
            }
        });

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

    // Temporary button
    public void onClickChat(View view) {
        Intent instrumentsIntent = new Intent(UserList.this, ChatActivity.class);
        UserList.this.startActivity(instrumentsIntent);
    }
}