package com.melodies.bandup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;

/**
 * A login screen that offers login via email/password.
 */

public class SoundCloud extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_cloud);
    }

    public void onSoundCloudSignIn(View view) {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }

        final ApiWrapper apiWrapper = new ApiWrapper("", "", null, null);

        final String username = ((EditText)findViewById(R.id.username)).getText().toString();
        final String password = ((EditText)findViewById(R.id.password)).getText().toString();

        // start new thread since requests cannot be made on main
        new Thread(new Runnable() {
            @Override
            public void run() {
                Token token = null;
                try {
                    token = apiWrapper.login(username, password, Token.SCOPE_NON_EXPIRING);
                    url = getResources().getString(R.string.api_address)
                            .concat("/login-soundcloud?access_token=")
                            .concat(token.access);

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("access_token", token.access);
                    jsonObject.put("email", username);

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.GET,
                            url,
                            jsonObject,
                            new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response.toString());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });

                    VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
