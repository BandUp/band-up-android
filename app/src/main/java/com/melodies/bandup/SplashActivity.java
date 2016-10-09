package com.melodies.bandup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

    private void isLoggedIn() {
        SharedPreferences srdPref = getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        System.out.println("SESSIONID:");
        System.out.println(srdPref.getString("sessionId", ""));

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                getResources().getString(R.string.api_address).concat("/isloggedin"),
                null, new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getBoolean("loggedIn")){
                        Intent userListIntent = new Intent(SplashActivity.this, MainScreenActivity.class);
                        SplashActivity.this.startActivity(userListIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    Intent intent = new Intent(SplashActivity.this, Login.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(SplashActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}