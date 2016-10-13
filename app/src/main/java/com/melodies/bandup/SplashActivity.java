package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;

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

    private void isLoggedIn() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getResources().getString(R.string.api_address).concat("/isloggedin"),
                null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getBoolean("loggedIn")){
                        openMainActivity();
                    } else {
                        openLoginActivity();
                    }
                } catch (JSONException e) {
                    openLoginActivity();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                openLoginActivity();
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}