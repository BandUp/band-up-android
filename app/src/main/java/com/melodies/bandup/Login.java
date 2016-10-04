package com.melodies.bandup;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.melodies.bandup.setup.Instruments;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    // server url location for login
    private String url;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";
    private ProgressDialog loginDialog;
    private EditText etUsername;
    private EditText etPassword;
    private LinearLayout mainLinearLayout;
    private LinearLayout linearLayoutInput;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;

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
        int activityHeight = mainLinearLayout.getHeight();
        int statusBarHeight = screenHeight - activityHeight;
        int paddingTop = getResources().getInteger(R.integer.login_image_padding_top);
        if (hasSoftNavigation(Login.this)) {
            return ((activityHeight - imageView.getHeight()) / 2 - statusBarHeight / 2 + getSoftButtonsBarHeight() / 2) - paddingTop;
        } else {
            return ((activityHeight - imageView.getHeight()) / 2 - statusBarHeight / 2) - paddingTop;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // need to initialize facebook before view
        setContentView(R.layout.activity_main);
        mainLinearLayout = (LinearLayout) findViewById(R.id.login_ll);
        linearLayoutInput = (LinearLayout) findViewById(R.id.login_ll_input);

        mainLinearLayout.post(new Runnable() {
            public void run() {
                mainLinearLayout.setY(getIconCenter());
                Animation testAnimation = AnimationUtils.loadAnimation(Login.this, R.anim.fade_in);
                mainLinearLayout.animate().translationY(0).setDuration(500);
                linearLayoutInput.startAnimation(testAnimation);
            }
        });

        String route = "/login-local";
        url = getResources().getString(R.string.api_address).concat(route);
        loginDialog = new ProgressDialog(Login.this);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);

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

        tilUsername = (TextInputLayout) findViewById(R.id.tilUsername);
        tilPassword = (TextInputLayout) findViewById(R.id.tilPassword);
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilUsername.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // -----------------------------Facebook START ------------------------------------------------------------

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button_facebook);

        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                //Toast.makeText(Login.this, loginResult.getAccessToken().getToken(), Toast.LENGTH_LONG).show();
                facebookCreateUser(loginResult);
            }

            @Override
            public void onCancel() {
                System.out.println("login canceled");
                Toast.makeText(Login.this, "login cancelled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("hit an error");
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // -----------------------------Google+ START -------------------------------------------------------------
        // Button listener
        //findViewById(R.id.login_button_google).setOnClickListener(this);
        //findViewById(R.id.sign_out_button).setOnClickListener(this);
        //findViewById(R.id.disconnect_button).setOnClickListener(this);

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
        SignInButton signInButton = (SignInButton) findViewById(R.id.login_button_google);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        // -----------------------------SoundCloud START -------------------------------------------------------------
        btnSoundCloud = (Button) findViewById(R.id.login_button_soundcloud);

        createLocationRequest();

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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    saveSessionId(response);
                    Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                    Login.this.startActivity(instrumentsIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                    finish();
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

    //
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
            Toast.makeText(getApplicationContext(), "Signed In ", Toast.LENGTH_SHORT).show();

            // Logged in, accessing user data
            GoogleSignInAccount acct = result.getSignInAccount();

            final String idToken = acct.getIdToken();
            String personName = acct.getDisplayName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            //Uri personPhoto = acct.getPhotoUrl();

            // Sending user info to server
            sendGoogleUserToServer(personId, idToken, personName, personEmail);
        }
    }

    // Sending user info to server
    private void sendGoogleUserToServer(String personId, String idToken, String personName, String personEmail) {
        try {
            url = getResources().getString(R.string.api_address).concat("/login-google");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("userId", personId);
            jsonObject.put("userToken", idToken);
            jsonObject.put("userName", personName);
            jsonObject.put("userEmail", personEmail);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(getApplicationContext(), "Success Response", Toast.LENGTH_SHORT).show();
                            saveSessionId(response);
                            Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                            Login.this.startActivity(instrumentsIntent);
                            finish();
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    errorHandlerLogin(error);

                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        } catch (JSONException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Google buttons
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button_google:
                signIn();
                break;
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

    // Google+ Sign Out
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    // Google+ Disconnecting Google account from the app
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient);
    }


    // ------------------------------Google+ END---------------------------------------------------------------

    // ------------------------------ SoundCloud---------------------------------------------------------------
    Button btnSoundCloud;

    private void soundcloudCreateUser() {
        url = getResources().getString(R.string.api_address).concat("/login-soundcloud");
        JSONObject jsonObject = new JSONObject();

        //TODO: Get user information from SoundCloud API

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url,
                jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                saveSessionId(response);
                Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                Login.this.startActivity(instrumentsIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                finish();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                VolleySingleton.getInstance(Login.this).checkCauseOfError(Login.this, error);
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void onClickSoundCloud(View v) {
        switch (v.getId()) {
            case R.id.login_button_soundcloud:
                // TODO: Connect to SoundCloud!
                break;
        }
    }
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
                System.out.println("ASDFASDF");
                tilPassword.setError(getString(R.string.login_password_validation));
            }

            if (isValid) {
                loginDialog = ProgressDialog.show(this, "Logging in", "Please wait...", true, false);
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
                        try {
                            Boolean hasFinishedSetup = response.getBoolean("hasFinishedSetup");
                            if (hasFinishedSetup) {
                                Intent userListIntent = new Intent(Login.this, UserList.class);
                                Login.this.startActivity(userListIntent);
                            } else {
                                Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                                Login.this.startActivity(instrumentsIntent);
                            }
                        } catch (JSONException e) {
                            Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                            Login.this.startActivity(instrumentsIntent);

                        } finally {
                            loginDialog.dismiss();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loginDialog.dismiss();
                        errorHandlerLogin(error);
                    }
                }
        );

        // insert request into queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    // Handling errors that can occur while SignIn request
    private void errorHandlerLogin(VolleyError error) {
        VolleySingleton.getInstance(Login.this).checkCauseOfError(Login.this, error);
    }

    // Storing user sessionId in SessionIdData folder, which only this app can access
    public void saveSessionId(JSONObject response) {
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        SharedPreferences srdPref = getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = srdPref.edit();
        editor.putString("sessionId", response.toString());
        editor.apply();
    }

    // when Sign Up is Clicked go to Registration View
    public void onClickSignUp(View v) {
        if (v.getId() == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(Login.this, Register.class);
            Login.this.startActivity(signUpIntent);
        }
    }

    // ======= Location setup ========
    private final int LOCATION_REQUEST_CODE = 333;

    protected void createLocationRequest() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request permissions
            ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        try{
            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));
            sendLocation(location);
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
    }

    private void sendLocation(Location location){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lon", location.getLongitude());
            jsonObject.put("lat", location.getLatitude());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url.concat("/location"), jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    createLocationRequest();
                } else {
                    // permission denied, boo!
                    Toast.makeText(this, "Need location for app functionality", Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    createLocationRequest();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}