package com.melodies.bandup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.GalleryPhoto;
import com.melodies.bandup.UserListController.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class UserList extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    UserListController ulc = new UserListController();
    private TextView txtName, txtStatus, txtDistance, txtPercentage, txtInstruments, txtGenres;
    private View     partialView;
    private CameraPhoto cameraPhoto;
    private GalleryPhoto galleryPhoto;
    final int CAMERA_REQUEST = 555;
    final int LIBRARY_REQUEST = 666;
    final int REQUEST_TIMEOUT = 10000;
    final int REQUEST_RETRY = 0;

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

        cameraPhoto = new CameraPhoto(this);
        galleryPhoto = new GalleryPhoto(this);

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
                                user.id = item.getString("_id");
                                user.name = item.getString("username");
                                user.status = item.getString("status");
                                user.distance = item.getInt("distance");
                                user.percentage = item.getInt("percentage");
                                user.imgURL = item.getJSONObject("image").getString("url");

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
                        if (ulc.users.size() > 0) {
                            displayUser(ulc.getUser(0));
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UserList.this, "Error.", Toast.LENGTH_LONG).show();
                        VolleySingleton.getInstance(UserList.this).checkCauseOfError(UserList.this, error);

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
            Intent toUserProfileIntent = new Intent(UserList.this, UserProfile.class);
            UserList.this.startActivity(toUserProfileIntent);

        } else if (id == R.id.nav_matches) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_about) {

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
                VolleySingleton.getInstance(UserList.this).checkCauseOfError(UserList.this, error);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("HELLOW");
        String url = getResources().getString(R.string.api_address).concat("/profile-picture");
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Toast.makeText(this, cameraPhoto.getPhotoPath(), Toast.LENGTH_SHORT).show();
                File image = new File(cameraPhoto.getPhotoPath());
                MultipartRequest multipartRequest = new MultipartRequest(url, image, "",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(UserList.this, "ImageSuccess", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UserList.this, "ImageError", Toast.LENGTH_SHORT).show();
                                VolleySingleton.getInstance(UserList.this).checkCauseOfError(UserList.this, error);
                            }
                        }
                );
                multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                        REQUEST_TIMEOUT,
                        REQUEST_RETRY,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);

            }
            if (requestCode == LIBRARY_REQUEST) {
                Uri uri = data.getData();
                System.out.println(uri);
                galleryPhoto.setPhotoUri(uri);
                File image = new File(uri.toString());

                MultipartRequest multipartRequest = new MultipartRequest(url, image, "",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(UserList.this, "ImageSuccess", Toast.LENGTH_SHORT).show();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(UserList.this, "ImageError", Toast.LENGTH_SHORT).show();
                                VolleySingleton.getInstance(UserList.this).checkCauseOfError(UserList.this, error);
                            }
                        }
                );
                VolleySingleton.getInstance(this).addToRequestQueue(multipartRequest);

                Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:
                boolean externalStorageAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean cameraAccepted = grantResults[1]==PackageManager.PERMISSION_GRANTED;
                if (externalStorageAccepted && cameraAccepted) {
                    try {
                        startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                        cameraPhoto.addToGallery();
                    } catch (IOException e) {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please allow access to the camera to take a photo.", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    public void onClickTakePicture(View view) {
        int permsRequestCode = 200;
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.CAMERA"};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(perms, permsRequestCode);
        } else {
            try {
                startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                cameraPhoto.addToGallery();
            } catch (IOException e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

    }

    public void onClickSelectPicture(View view) {
//        int permsRequestCode = 300;
//        String[] perms = {"android.permission.READ_EXTERNAL_STORAGE"};
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//            requestPermissions(perms, permsRequestCode);
//        } else {
//            startActivityForResult(galleryPhoto.openGalleryIntent(), LIBRARY_REQUEST);
//        }
    }
}