package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.setup.Instruments;
import com.melodies.bandup.setup.SetupShared;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * A login screen that offers login via email/password.
 */

public class SoundCloud extends AppCompatActivity implements DatePickable {
    private String url;
    private AdView mAdView;
    private Date dateOfBirth = null;
    private DatePickerFragment datePickerFragment = null;
    SetupShared sShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_cloud);
        sShared = new SetupShared();
        getAd();
    }

    // Adding ad Banner
    private void getAd() {
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onSoundCloudSignIn(View view) {
        final ApiWrapper apiWrapper = new ApiWrapper(
                getResources().getString(R.string.soundCloudClient),
                getResources().getString(R.string.soundCloudSecret)
                , null, null);

        final String username = ((EditText)findViewById(R.id.username)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString();

        // start new thread since requests cannot be made on main
        new Thread(new Runnable() {
            @Override
            public void run() {
                Token token = null;
                try {
                    token = apiWrapper.login(username, password, Token.SCOPE_NON_EXPIRING);
                    url = getResources().getString(R.string.api_address)
                            .concat("/login-soundcloud?access_token=")
                            .concat(token.access);

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("access_token", token.access);
                    jsonObject.put("email", username);
                    jsonObject.put("dateOfBirth", dateOfBirth);

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.GET,
                            url,
                            jsonObject,
                            new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            sShared.saveUserId(SoundCloud.this, response);
                            openCorrectIntent(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void openCorrectIntent(JSONObject response) {
        Boolean hasFinishedSetup = null;
        try {
            hasFinishedSetup = response.getBoolean("hasFinishedSetup");
            if (hasFinishedSetup) {
                Intent userListIntent = new Intent(SoundCloud.this, MainScreenActivity.class);
                SoundCloud.this.startActivity(userListIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            } else {
                Intent instrumentsIntent = new Intent(SoundCloud.this, Instruments.class);
                SoundCloud.this.startActivity(instrumentsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            }
        } catch (JSONException e) {
            Intent instrumentsIntent = new Intent(SoundCloud.this, Instruments.class);
            SoundCloud.this.startActivity(instrumentsIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
            finish();
        }
    }

    @Override
    public void onDateSet(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        // Calendar to Date object.
        dateOfBirth = cal.getTime();

        datePickerFragment.ageCalculator(year, month, day);

        Intent instrumentsIntent = new Intent(SoundCloud.this, Instruments.class);
        SoundCloud.this.startActivity(instrumentsIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
        finish();
    }
}

