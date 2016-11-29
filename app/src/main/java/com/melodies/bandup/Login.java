package com.melodies.bandup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Instruments;
import com.melodies.bandup.setup.SetupShared;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, DatePickable {
    // server url location for login
    private String url;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private ProgressDialog loginDialog;
    private EditText etUsername;
    private EditText etPassword;
    private LinearLayout linearLayoutParent;
    private LinearLayout mainLinearLayout;
    private LinearLayout linearLayoutInput;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;
    private SetupShared sShared;
    private Date dateOfBirth = null;
    private DatePickerFragment datePickerFragment = null;
    private Button btnSignIn;
    private Button btnSignUp;

    private CallbackManager callbackManager = CallbackManager.Factory.create();

    private static boolean hasSoftNavigation(Context context) {
        return !ViewConfiguration.get(context).hasPermanentMenuKey();
    }

    private int getSoftButtonsBarHeight() {
        DisplayMetrics metrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;

        getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;

        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    private int getIconCenter() {
        final ImageView imageView = (ImageView) findViewById(R.id.band_up_login_logo);

        int screenHeight = Login.this.getResources().getDisplayMetrics().heightPixels;
        int parentHeight = linearLayoutParent.getHeight();
        int activityHeight = mainLinearLayout.getHeight();
        int statusBarHeight = screenHeight - parentHeight;
        int paddingTop = getResources().getInteger(R.integer.login_image_padding_top);
        System.out.println("HEIGHTS");
        System.out.println(parentHeight);
        System.out.println(statusBarHeight);
        System.out.println(activityHeight);
        System.out.println(parentHeight - activityHeight);
        if (hasSoftNavigation(Login.this)) {
            return ((activityHeight - imageView.getHeight()) / 2 - statusBarHeight / 2 + getSoftButtonsBarHeight() / 2) - paddingTop + (parentHeight-activityHeight);
        } else {
            return ((activityHeight - imageView.getHeight()) / 2 - statusBarHeight / 2) - paddingTop + (parentHeight - activityHeight);
        }
    }


    protected void setGooglePlusButtonText(SignInButton signInButton,
                                           String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setTextSize(15);
                tv.setTypeface(null, Typeface.NORMAL);
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // need to initialize facebook before view
        setContentView(R.layout.activity_login);
        mainLinearLayout = (LinearLayout) findViewById(R.id.login_ll);
        linearLayoutParent = (LinearLayout) findViewById(R.id.login_parent_ll);
        linearLayoutInput = (LinearLayout) findViewById(R.id.login_ll_input);
        sShared = new SetupShared();

        getAd();


        String route = "/login-local";
        url = getResources().getString(R.string.api_address).concat(route);
        loginDialog = new ProgressDialog(Login.this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        TextView otherServs = (TextView) findViewById(R.id.login_other_services_hint);
        Button btnForgPass = (Button) findViewById(R.id.btn_forgot_password);


        btnSignIn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        tilUsername.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        tilPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        etUsername.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        etPassword.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        btnSignUp.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));
        otherServs.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf"));
        btnForgPass.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf"));


        etPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    try {
                        Login.this.onClickSignIn(findViewById(R.id.btnSignIn));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
                return false;
            }
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

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setErrorEnabled(false);
            }
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        });

        // -----------------------------Facebook START ------------------------------------------------------------

        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);

        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                facebookCreateUser(loginResult);
            }

            @Override
            public void onCancel() {
                System.out.println("Login Cancelled");
                Toast.makeText(Login.this, "Login Cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("hit an error");
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        LinearLayout llFacebookLoginDesign = (LinearLayout) findViewById(R.id.login_button_facebook_design);
        llFacebookLoginDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButton.performClick();
            }
        });

        // -----------------------------Google+ START -------------------------------------------------------------
        // Button listener

        // configuring simple Google+ sign in requesting userId and email and basic profile (included in DEFAULT_SIGN_IN)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestId()
                .requestEmail()
                .build();

        // Google client with access to the Google SignIn API and the options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Google+ Sign In button design
        LinearLayout llGoogleLoginDesign = (LinearLayout) findViewById(R.id.login_button_google_design);
        llGoogleLoginDesign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // -----------------------------SoundCloud START -------------------------------------------------------------
        btnSoundCloud = (LinearLayout) findViewById(R.id.login_button_soundcloud);
        btnSoundCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.login_button_soundcloud:
                        Intent intent = new Intent(Login.this, SoundCloud.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    // creates advertisment
    private void getAd() {
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
        AdView mAdview = (AdView)findViewById(R.id.adView);
        AdRequest mAdRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) // this line makes ads on emulator
                .build();
        mAdview.loadAd(mAdRequest);
    }

    /**
     * take result from facebook login process and create user in backend
     * <p>
     * side effect: starts up instrument activity if succesfull
     *
     * @param loginResult facebook loginResult
     */
    private void facebookCreateUser(LoginResult loginResult) {
        try {
            url = getResources().getString(R.string.api_address).concat("/login-facebook");
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("access_token", loginResult.getAccessToken().getToken());
            jsonObject.put("dateOfBirth", dateOfBirth);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    sShared.saveUserId(Login.this, response);
                    openCorrectIntent(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    errorHandlerLogin(error);
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Accessing user data from Google & storing on server
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Toast.makeText(getApplicationContext(), "Signed In", Toast.LENGTH_SHORT).show();

            // Logged in, accessing user data
            GoogleSignInAccount acct = result.getSignInAccount();

            final String idToken = acct.getIdToken();

            sendGoogleUserToServer(idToken);
        }
    }
    private void openCorrectIntent(JSONObject response) {
        Boolean hasFinishedSetup = null;
        try {
            hasFinishedSetup = response.getBoolean("hasFinishedSetup");
            if (hasFinishedSetup) {
                Intent userListIntent = new Intent(Login.this, MainScreenActivity.class);
                Login.this.startActivity(userListIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            } else {
                //showDatePickerDialog();
                Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                Login.this.startActivity(instrumentsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            }
        } catch (JSONException e) {
            Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
            Login.this.startActivity(instrumentsIntent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
            finish();
        }
    }

    public void showDatePickerDialog() {
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    // Sending user info to server
    private void sendGoogleUserToServer(String idToken) {
        try {
            url = getResources().getString(R.string.api_address).concat("/login-google");
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("access_token", idToken);
            jsonObject.put("dateOfBirth", dateOfBirth);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            sShared.saveUserId(Login.this, response);
                            openCorrectIntent(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorHandlerLogin(error);
                        }
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Unresorvable error occured and Google API will not be available
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(getApplicationContext(), "Google+ SignIn Error!", Toast.LENGTH_SHORT).show();
    }

    // Google+ Sign In
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // ------------------------------Google+ END---------------------------------------------------------------

    // ------------------------------ SoundCloud---------------------------------------------------------------
    LinearLayout btnSoundCloud;
    // ------------------------------SoundCloud END ---------------------------------------------------------------

    // when Sign In is Clicked grab data and ...
    public void onClickSignIn(View v) throws JSONException {
        // catching views into variables

        // converting into string
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (v.getId() == R.id.btnSignIn) {
            // Check for empty field in the form
            Boolean isValid = true;

            if (username.isEmpty()) {
                isValid = false;
                tilUsername.setError(getString(R.string.login_username_validation));
                etUsername.requestFocus();
            }
            if (password.isEmpty()) {
                isValid = false;
                tilPassword.setError(getString(R.string.login_password_validation));
            }
            if (isValid) {
                loginDialog = ProgressDialog.show(this, getString(R.string.login_progress_title), getString(R.string.login_progress_description), true, false);
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

        DatabaseSingleton.getInstance(getApplicationContext()).getBandUpDatabase().local_login(
                user,
                new BandUpResponseListener() {
                    @Override
                    public void onBandUpResponse(Object response) {
                        JSONObject responseObj = null;
                        if (response instanceof JSONObject) {
                            responseObj = (JSONObject) response;
                        }

                        sShared.saveUserId(Login.this, responseObj);
                        Toast.makeText(Login.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                        openCorrectIntent(responseObj);
                        loginDialog.dismiss();

                    }
                },
                new BandUpErrorListener() {
                    @Override
                    public void onBandUpErrorResponse(VolleyError error) {
                        loginDialog.dismiss();
                        errorHandlerLogin(error);
                    }
                }
        );
    }

    public void onResetPassword(View v){
        Intent paswordResetIntent = new Intent(Login.this, PasswordReset.class);
        Login.this.startActivity(paswordResetIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
    }

    // Handling errors that can occur while SignIn request
    private void errorHandlerLogin(VolleyError error) {
        if (error instanceof AuthFailureError) {
            Toast.makeText(this, R.string.login_credentials_incorrect, Toast.LENGTH_SHORT).show();
            return;
        }

        VolleySingleton.getInstance(Login.this).checkCauseOfError(error);
    }

    // when Sign Up is Clicked go to Registration View
    public void onClickSignUp(View v) {
        if (v.getId() == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(Login.this, Register.class);
            Login.this.startActivity(signUpIntent);
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

        Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
        Login.this.startActivity(instrumentsIntent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
        finish();
    }
}