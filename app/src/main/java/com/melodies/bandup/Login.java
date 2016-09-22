package com.melodies.bandup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    // server url location for login
    private String url;

    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "SignInActivity";

    private CallbackManager callbackManager = CallbackManager.Factory.create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext()); // need to initialize facebook before view
        setContentView(R.layout.activity_main);
        String route = "/login-local";
        url = getResources().getString(R.string.api_address).concat(route);

    // -----------------------------Facebook START ------------------------------------------------------------


        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

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
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

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
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

    }

    /**
     * take result from facebook login process and create user in backend
     *
     * side effect: starts up instrument activity if succesfull
     *
     * @param loginResult facebook loginResult
     */
    private void facebookCreateUser(LoginResult loginResult) {
        try{
            url = getResources().getString(R.string.api_address).concat("/login-facebook");
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("access_token", loginResult.getAccessToken().getToken());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url,
                    jsonObject, new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    saveSessionId(response);
                    Intent instrumentsIntent = new Intent(Login.this, Instruments.class);
                    Login.this.startActivity(instrumentsIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.no_change);
                    finish();
                }
            }, new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(Login.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        }catch (JSONException ex){
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
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // Sign In result handler
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            Toast.makeText(getApplicationContext(), "Signed In ", Toast.LENGTH_SHORT).show();

            // Logged in, accessing user data
            GoogleSignInAccount acct = result.getSignInAccount();

            final String idToken = acct.getIdToken();
            //Toast.makeText(getApplicationContext(), "idToken: "+idToken, Toast.LENGTH_SHORT).show();
            // send token to sever and validate server side
            sendGoogleTokenToServer(idToken);

            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
        }
    }

    private void sendGoogleTokenToServer(String idToken) {
        // create request for Login
        JSONObject user = new JSONObject();
        try {
            user.put("idToken", idToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                getResources().getString(R.string.google_token),
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Login.this, "User added server side, token valid", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Login.this, error.toString(), Toast.LENGTH_SHORT).show();
                        errorHandlerLogin(error);
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    //
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                Toast.makeText(getApplicationContext(), "You are Signed Out", Toast.LENGTH_SHORT).show();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                Toast.makeText(getApplicationContext(), "Google+ disconnected from the app!", Toast.LENGTH_LONG).show();
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
    private void signOut() { Auth.GoogleSignInApi.signOut(mGoogleApiClient); }

    // Google+ Disconnecting Google account from the app
    private void revokeAccess() { Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient); }



    // ------------------------------Google+ END ---------------------------------------------------------------



    // Go to chat button temp----------------------------
    public void onClickGoToChat (View v) {
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
        System.out.println(user.toString());
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

}