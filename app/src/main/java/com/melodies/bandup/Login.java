package com.melodies.bandup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    // server url location for login
    private String url = "https://band-up-server.herokuapp.com/login-local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    // Go to chat button temp----------------------------
    public void onClickGoToChat (View v) {
        final Button btnGoToChat = (Button) findViewById(R.id.btnGoToChat);
        if (v.getId() == R.id.btnGoToChat) {
            Intent toChatIntent = new Intent(Login.this, ChatActivity.class);
            Login.this.startActivity(toChatIntent);
        }
    }
    //--------------------------------------------

    // when Sign In is Clicked grab data and ...
    public void onClickSignIn(View v) throws JSONException {
        // catching views into variables
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);

        // converting into string
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (v.getId() == R.id.btnSignIn) {
            // Check for empty field in the form
            if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Username.", Toast.LENGTH_SHORT).show();
            }
            else if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Password.", Toast.LENGTH_SHORT).show();
            }
            else {
                createloginRequest(username, password);
            }
        }
    }

    // Login user into app
    private void createloginRequest(String username, String password) {
        // create request for Login
        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        saveSessionId(response);
                        Toast.makeText(Login.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                        Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                        Login.this.startActivity(instrumentsIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorHandlerLogin(error);
                    }
                }
        );

        // insert request into queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Handling errors that can occur while SignIn request
    private void errorHandlerLogin(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(Login.this, "Connection error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof AuthFailureError ) {
            Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ServerError) {
            Toast.makeText(Login.this, "Server error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof NetworkError) {
            Toast.makeText(Login.this, "Network error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ParseError) {
            Toast.makeText(Login.this, "Server parse error!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(Login.this, "Unknown error! Contact Administrator", Toast.LENGTH_LONG).show();
        }
    }

    // Storing user sessionId in SessionIdData folder, which only this app can access
    public void saveSessionId(JSONObject response) {

        SharedPreferences srdPref = getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = srdPref.edit();
        editor.putString("sessionId", response.toString());
        editor.commit();
    }

    // when Sign Up is Clicked go to Registration View
    public void onClickSignUp(View v) {
        final Button btnSignUp = (Button) findViewById(R.id.btnSignUp);

        if (v.getId() == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(Login.this, Register.class);
            Login.this.startActivity(signUpIntent);
        }
    }
}