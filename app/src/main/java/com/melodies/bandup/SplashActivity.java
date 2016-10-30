package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Instruments;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLoggedIn();
    }

    private void openLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent userListIntent = new Intent(SplashActivity.this, MainScreenActivity.class);
        SplashActivity.this.startActivity(userListIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void openSetupActivities() {
        Intent userListIntent = new Intent(SplashActivity.this, Instruments.class);
        SplashActivity.this.startActivity(userListIntent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void isLoggedIn() {
        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().isLoggedIn(
                new BandUpResponseListener() {
                    @Override
                    public void onBandUpResponse(Object response) {
                        JSONObject responseObj = null;
                        if (response instanceof JSONObject) {
                            responseObj = (JSONObject) response;
                        }
                        try {
                            if (!responseObj.isNull("isLoggedIn")) {
                                if (responseObj.getBoolean("isLoggedIn")){
                                    if (!responseObj.isNull("hasFinishedSetup")) openSetupActivities();
                                    else openMainActivity();
                                } else {
                                    openLoginActivity();
                                }
                            } else {
                                openLoginActivity();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new BandUpErrorListener() {
                    @Override
                    public void onBandUpErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "server error", Toast.LENGTH_LONG).show();
                        openLoginActivity();
                    }
                }
        );
    }
}