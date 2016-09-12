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

public class Register extends AppCompatActivity {
    private String url = "https://band-up-server.herokuapp.com/signup-local";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    //
    public void onClickRegister(View v) throws JSONException {
        // binding vire to variables
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
        final EditText etAge = (EditText) findViewById(R.id.etAge);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        // converting to string
        final String email = etEmail.getText().toString();
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String password2 = etPassword2.getText().toString();
        final String age = etAge.getText().toString();

        // when button Register is pushed:
        if (v.getId() == R.id.btnRegister) {
            // check if passwords match
            if (!password.equals(password2)) {
                Toast.makeText(Register.this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            }
            /*
            else if (email.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Email.", Toast.LENGTH_SHORT).show();
            }
            */
            else if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Username.", Toast.LENGTH_SHORT).show();
            }
            else if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Password.", Toast.LENGTH_SHORT).show();
            }
            /*
            else if (age.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Please enter your Age.", Toast.LENGTH_SHORT).show();
            }
            */
            else {
                // create request
                createRegisterRequest(username, password);
            }
        }
    }

    // creating user registration form and sending request to server
    public void createRegisterRequest(String username, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            //jsonObject.put("email", email);
            //jsonObject.put("age", age);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObject,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        // if response is not error, then userId is stored and redirect to SignIn view.
                        saveUserId(response);
                        System.out.println("\"Registration succesful!");
                        Toast.makeText(Register.this, "Registration succesful! You can Sign In now.", Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(Register.this, Login.class);
                        Register.this.startActivity(registerIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        errorHandlerRegister(error);
                    }
                }
        );
        // send request
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void errorHandlerRegister(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(Register.this, "Connection error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof AuthFailureError) {
            Toast.makeText(Register.this, "Invalid username!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ServerError) {
            Toast.makeText(Register.this, "Server error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof NetworkError) {
            Toast.makeText(Register.this, "Network error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ParseError) {
            Toast.makeText(Register.this, "Server parse error!", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(Register.this, "Unknown error! Contact Administrator", Toast.LENGTH_LONG).show();
        }
    }

    // Storing user userId in UserIdData folder, which only this app can access
    public void saveUserId(JSONObject response) {

        SharedPreferences srdPref = getSharedPreferences("UserIdData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = srdPref.edit();
        editor.putString("userId", response.toString());
        editor.commit();
    }


}

