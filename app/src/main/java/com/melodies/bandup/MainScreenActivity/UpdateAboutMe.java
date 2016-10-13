package com.melodies.bandup.MainScreenActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

public class UpdateAboutMe extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateaboutme);
    }

    // Send user AboutMe data to server
    public void updateUser(String id, String aboutMe) {
        JSONObject user = new JSONObject();
        try {
            user.put("userId", id);
            user.put("aboutMe", aboutMe);
            Toast.makeText(UpdateAboutMe.this, "sending: "+user, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = getResources().getString(R.string.api_address).concat("/edit-user");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                user,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            Toast.makeText(UpdateAboutMe.this, "User Updated", Toast.LENGTH_LONG).show();
                            //Intent goBack = new Intent(UpdateAboutMe.this, ProfileFragment.class);
                            //UpdateAboutMe.this.startActivity(goBack);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(UpdateAboutMe.this, "Bad response: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        );
        // insert request into queue
        VolleySingleton.getInstance(UpdateAboutMe.this).addToRequestQueue(jsonObjectRequest);
    }

    // get users essay and send it to server
    public void onClickSave(View view) throws JSONException {
        final EditText etAboutMe = (EditText)findViewById(R.id.etAboutMe);
        String about = etAboutMe.getText().toString();
        if (!about.isEmpty()) {
            Toast.makeText(UpdateAboutMe.this, "Updating User", Toast.LENGTH_SHORT).show();
            updateUser(getUserId(), about);
        }
    }

    // get user ID
    public String getUserId() throws JSONException {
        SharedPreferences srdPref = UpdateAboutMe.this.getSharedPreferences("SessionIdData", Context.MODE_PRIVATE);
        String response = srdPref.getString("response", "N/A");
        JSONObject obj = new JSONObject(response);
        String id = obj.get("userID").toString();
        return (!id.equals("N/A")) ? id : "No data Found";
    }
}
