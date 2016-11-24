package com.melodies.bandup.MainScreenActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.DatabaseSingleton;
import com.melodies.bandup.R;
import com.melodies.bandup.helper_classes.User;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.melodies.bandup.MainScreenActivity.ProfileFragment.DEFAULT;

public class UpdateAboutMe extends AppCompatActivity {

    User currentUser;
    private AdView mAdView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaboutme);
        setTitle("About Me");
        userRequest();

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Adding ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = (AdView)findViewById(R.id.adView);
        mAdView.loadAd(adRequest);

    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    // Get the userid of logged in user
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
        String id = srdPref.getString("userID", DEFAULT);
        return (!id.equals(DEFAULT)) ? id : "User ID Not Found";
    }

    // Request REAL user info from server
    public void userRequest() {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", getUserId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DatabaseSingleton.getInstance(UpdateAboutMe.this).getBandUpDatabase().getUserProfile(user, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                }
                currentUser = new User();
                    try {
                        // putting user description into EditText
                        if (!responseObj.isNull("aboutme")) {
                            EditText et = (EditText) findViewById(R.id.etAboutMe);
                            String s = currentUser.aboutme = responseObj.getString("aboutme");
                            et.setText(s);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                System.out.println("ERROR");
            }
        });
    }

    // Send user AboutMe data to server
    public void updateUser(String id, final String aboutMe) {
        JSONObject userUpdated = new JSONObject();
        try {
            userUpdated.put("_id", id);
            userUpdated.put("aboutme", aboutMe);

            DatabaseSingleton.getInstance(this).getBandUpDatabase().updateUser(userUpdated, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                // we were successful send about me data to previous view:
                Intent i = new Intent();
                i.putExtra("MESSAGE", aboutMe);
                if (aboutMe.isEmpty()) {
                    i.putExtra("MESSAGE", "About Me");
                }
                setResult(2, i);
                onBackPressed();
            }
            }, new BandUpErrorListener() {
                @Override
                public void onBandUpErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Update About Me and send it to server
    public void onClickSave(View view) throws JSONException {
        final EditText etAboutMe = (EditText)findViewById(R.id.etAboutMe);
        String about = etAboutMe.getText().toString();

        if (!about.isEmpty()) {
            updateUser(getUserId(), about);
        }
        else if (about.isEmpty()) {
            updateUser(getUserId(), "About Me");
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
