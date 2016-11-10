package com.melodies.bandup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class Register extends AppCompatActivity implements DatePickable {
    private String url;
    private String route = "/signup-local";
    private ProgressDialog registerDialog;
    private TextView txtDateOfBirth;
    private Date dateOfBirth = null;
    private DatePickerFragment datePickerFragment = null;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getResources().getString(R.string.api_address).concat(route);
        setContentView(R.layout.activity_register);
        registerDialog = new ProgressDialog(Register.this);
        setTitle(getString(R.string.register_title));
        txtDateOfBirth = (TextView) findViewById(R.id.txtDateOfBirth);
        getAd();
    }

    // Adding ad Banner
    private void getAd() {
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void showDatePickerDialog(View v) {
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    public void onDateSet(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);

        // Calendar to Date object.
        dateOfBirth = cal.getTime();

        // Get the locale date format.
        java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(Register.this);

        // Formatted date.
        String date = dateFormat.format(dateOfBirth);

        String age = datePickerFragment.ageCalculator(year, month, day);

        String dateString = String.format("%s (%s)", date, age);
        txtDateOfBirth.setText(dateString);
    }

    public void onClickRegister(View v) throws JSONException {
        // binding vire to variables
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);

        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        // converting to string
        final String email = etEmail.getText().toString();
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String password2 = etPassword2.getText().toString();

        // when button Register is pushed:
        if (v.getId() == R.id.btnRegister) {
            // check if passwords match
            if (!password.equals(password2)) {
                Toast.makeText(Register.this, R.string.register_password_mismatch, Toast.LENGTH_SHORT).show();
            }
            else if (email.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.register_enter_email, Toast.LENGTH_SHORT).show();
            }
            else if (username.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.register_enter_username, Toast.LENGTH_SHORT).show();
            }
            else if (password.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.register_enter_password, Toast.LENGTH_SHORT).show();
            }
            else if (dateOfBirth == null) {
                Toast.makeText(getApplicationContext(), R.string.register_enter_dateofbirth, Toast.LENGTH_SHORT).show();
            }
            else {
                registerDialog = ProgressDialog.show(this, getString(R.string.register_progress_title), getString(R.string.register_progress_description), true, false);
                // create request
                createRegisterRequest(username, password, email, dateOfBirth);
            }
        }
    }

    // creating user registration form and sending request to server
    public void createRegisterRequest(String username, String password, String email, Date dateOfBirth) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", username);
            jsonObject.put("password", password);
            jsonObject.put("email", email);
            jsonObject.put("dateOfBirth", dateOfBirth);
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
                        System.out.println("\"Registration successful!");
                        Toast.makeText(Register.this, R.string.register_success, Toast.LENGTH_LONG).show();
                        Intent registerIntent = new Intent(Register.this, Login.class);
                        registerDialog.dismiss();
                        Register.this.startActivity(registerIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        registerDialog.dismiss();
                        errorHandlerRegister(error);
                    }
                }
        );
        // send request
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Handling errors that can occur while Sign Up request
    private void errorHandlerRegister(VolleyError error) {
        VolleySingleton.getInstance(Register.this).checkCauseOfError(error);
    }

    // Storing userId in UserIdData folder, which only this app can access
    public void saveUserId(JSONObject response) {
        try {
            String id = response.get("id").toString();
            SharedPreferences srdPref = getSharedPreferences("UserIdRegister", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = srdPref.edit();
            editor.putString("userId", id);
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

