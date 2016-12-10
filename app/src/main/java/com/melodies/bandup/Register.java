package com.melodies.bandup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import static com.melodies.bandup.R.id.etPassword;
import static com.melodies.bandup.R.id.tilDateOfBirth;

public class Register extends AppCompatActivity implements DatePickable {
    private ProgressDialog registerDialog;
    private Date dateOfBirth = null;
    private DatePickerFragment datePickerFragment = null;
    private TextInputLayout tilEmail, tilUsername, tilPassword1, tilPassword2, tilDob;
    private EditText etEmail, etUsername, etPassword1, etPassword2, etDateOfBirth;
    private ImageView ivSuccess, ivError;
    private ProgressBar progEmailLoading;

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }
    private void showDatePicker() {
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }
        datePickerFragment.show(getFragmentManager(), "datePicker");
        tilPassword2.clearFocus();
    }

    private void initializeOnClickListeners() {
        etDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void initializeOnFocusChangeListeners() {
        etEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String textValue = etEmail.getText().toString();
                if (!hasFocus) {
                    if (textValue.isEmpty()) {
                        tilEmail.setError(getString(R.string.register_til_error_fill_email));
                    } else if (isValidEmail(textValue)) {
                        JSONObject email = new JSONObject();
                        try {
                            email.put("email", textValue);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ivSuccess.setVisibility(View.INVISIBLE);
                        ivError.setVisibility(View.INVISIBLE);
                        progEmailLoading.setVisibility(View.VISIBLE);
                        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().getEmailInUse(email, new BandUpResponseListener() {
                            @Override
                            public void onBandUpResponse(Object response) {
                                progEmailLoading.setVisibility(View.INVISIBLE);
                                JSONObject responseObj = null;
                                if (response instanceof JSONObject) {
                                    responseObj = (JSONObject) response;
                                }
                                if (responseObj == null) {
                                    return;
                                }
                                if (isValidEmail(etEmail.getText().toString())) {
                                    try {
                                        Boolean emailInUse;
                                        if (!responseObj.isNull("emailInUse")) emailInUse = responseObj.getBoolean("emailInUse");
                                        else return;

                                        if (!emailInUse) {
                                            ivError.setVisibility(View.INVISIBLE);
                                            ivSuccess.setVisibility(View.VISIBLE);
                                        } else {
                                            tilEmail.setError(getString(R.string.register_til_error_email_linked));
                                            ivError.setVisibility(View.VISIBLE);
                                            ivSuccess.setVisibility(View.INVISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, new BandUpErrorListener() {
                            @Override
                            public void onBandUpErrorResponse(VolleyError error) {
                                progEmailLoading.setVisibility(View.INVISIBLE);
                                VolleySingleton.getInstance(Register.this).checkCauseOfError(error);
                            }
                        });
                    } else if (!isValidEmail(textValue)) {
                        tilEmail.setError(getString(R.string.register_til_error_email_format));
                    } else {
                        tilEmail.setErrorEnabled(false);
                    }
                }
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String textValue = etUsername.getText().toString();
                if (!hasFocus) {
                    if (textValue.isEmpty()) {
                        tilUsername.setError(getString(R.string.register_til_error_username));
                    }
                }
            }
        });

        etPassword1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String textValue = etPassword1.getText().toString();
                if (!hasFocus) {
                    if (textValue.isEmpty()) {
                        tilPassword1.setError(getString(R.string.register_til_error_fill_password));
                    } else if (textValue.length() < 6) {
                        tilPassword1.setError(getString(R.string.register_til_error_password_length));
                    }
                }
            }
        });

        etPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pass1 = etPassword1.getText().toString();
                    String pass2 = etPassword2.getText().toString();

                    if (!pass1.equals(pass2)) {
                        tilPassword2.setError(getString(R.string.register_til_error_password_mismatch));
                    } else {
                        tilPassword2.setError(null);
                    }
                }
            }
        });
    }

    private void initializeOnTextChangedListeners() {
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String checkString = s.toString();
                if (isValidEmail(checkString)) {
                    ivSuccess.setVisibility(View.INVISIBLE);
                    ivError.setVisibility(View.INVISIBLE);
                    progEmailLoading.setVisibility(View.INVISIBLE);
                    tilEmail.setErrorEnabled(false);
                } else {
                    ivSuccess.setVisibility(View.INVISIBLE);
                    ivError.setVisibility(View.INVISIBLE);
                    progEmailLoading.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilUsername.setErrorEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword1.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword2.setErrorEnabled(false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void initializeViews() {
        etEmail       = (EditText) findViewById(R.id.etEmail);
        etUsername    = (EditText) findViewById(R.id.etUsername);
        etPassword1   = (EditText) findViewById(etPassword);
        etPassword2   = (EditText) findViewById(R.id.etPassword2);
        etDateOfBirth = (EditText) findViewById(R.id.etDateOfBirth);

        tilEmail     = (TextInputLayout) findViewById(R.id.tilEmail);
        tilUsername  = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword1 = (TextInputLayout) findViewById(R.id.tilPassword1);
        tilPassword2 = (TextInputLayout) findViewById(R.id.tilPassword2);
        tilDob       = (TextInputLayout) findViewById(tilDateOfBirth);

        ivError   = (ImageView) findViewById(R.id.emailValidationError);
        ivSuccess = (ImageView)  findViewById(R.id.emailValidationSuccess);

        progEmailLoading = (ProgressBar) findViewById(R.id.emailValidationLoading);
    }


    private void initializeOnKeyListeners() {
        etPassword2.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    showDatePicker();
                    return true;
                }
                return false;
            }
        });

        etPassword2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (v.getId() == R.id.etPassword2) {
                        showDatePicker();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initializeViews();
        initializeOnClickListeners();
        initializeOnTextChangedListeners();
        initializeOnFocusChangeListeners();
        initializeOnKeyListeners();

        registerDialog = new ProgressDialog(Register.this);
        setTitle(getString(R.string.register_title));
        getAd();
        goBackTitle();
    }

    private void goBackTitle() {
        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    // Adding ad Banner
    private void getAd() {
        AdView mAdView;
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public void onDateSet(int year, int month, int day) {
        tilDob.setErrorEnabled(false);
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
        etDateOfBirth.setText(dateString);
    }

    private Boolean formIsClean() {
        return  !tilEmail.isErrorEnabled()     &&
                !tilUsername.isErrorEnabled()  &&
                !tilPassword1.isErrorEnabled() &&
                !tilPassword2.isErrorEnabled() &&
                !tilDob.isErrorEnabled();
    }
    public void onClickRegister(View v) throws JSONException {
        // binding vire to variables

        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        // converting to string
        final String email     = etEmail    .getText().toString();
        final String username  = etUsername .getText().toString();
        final String password  = etPassword1.getText().toString();
        final String password2 = etPassword2.getText().toString();

        // when button Register is pushed:
        if (v.getId() == R.id.btnRegister) {
            // check if passwords match

            if (email.isEmpty()) {
                tilEmail.setError(getString(R.string.register_til_error_fill_email));
                etEmail.requestFocus();
            }
            else if (username.isEmpty()) {
                tilUsername.setError(getString(R.string.register_til_error_username));
                etUsername.requestFocus();
            }
            else if (password.isEmpty()) {
                tilPassword1.setError(getString(R.string.register_til_error_fill_password));
                etPassword1.requestFocus();
            }
            else if (password2.isEmpty() && !password.isEmpty()) {
                tilPassword2.setError(getString(R.string.register_til_error_fill_password_again));
                etPassword2.requestFocus();
            }
            else if (!password.equals(password2)) {
                tilPassword1.setError(getString(R.string.register_password_mismatch));
            }
            else if (dateOfBirth == null) {
                tilDob.setError(getString(R.string.register_enter_dateofbirth));
            }
            else if (!formIsClean()) {
                Toast.makeText(getApplicationContext(), R.string.register_fix_errors, Toast.LENGTH_SHORT).show();
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

        DatabaseSingleton.getInstance(Register.this).getBandUpDatabase().register(jsonObject, new BandUpResponseListener() {
            @Override
            public void onBandUpResponse(Object response) {
                JSONObject responseObj = null;
                if (response instanceof JSONObject) {
                    responseObj = (JSONObject) response;
                }
                // if response is not error, then userId is stored and redirect to SignIn view.
                saveUserId(responseObj);
                Toast.makeText(Register.this, R.string.register_success, Toast.LENGTH_LONG).show();
                registerDialog.dismiss();
                finish();
            }
        }, new BandUpErrorListener() {
            @Override
            public void onBandUpErrorResponse(VolleyError error) {
                registerDialog.dismiss();
                errorHandlerRegister(error);
            }
        });
    }

    // Handling errors that can occur while Sign Up request
    private void errorHandlerRegister(VolleyError error) {
        if (error instanceof AuthFailureError) {
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            return;
        }
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

