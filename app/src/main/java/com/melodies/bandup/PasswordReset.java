package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordReset extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);
    }

    /**
     * create a request for password reset
     * notify user to check e-mail if succesfull
     * otherwise show an apropriate notification
     *
     * @param v
     */
    public void onSendPasswordReset(View v){
        try{
            JSONObject requestObj = new JSONObject();
            requestObj.put("email", ((EditText)findViewById(R.id.txt_email_reset)).getText().toString());
            DatabaseSingleton.getInstance(this).getBandUpDatabase().sendPasswordResetRequest(
                    requestObj,
                    new BandUpResponseListener() {
                        @Override
                        public void onBandUpResponse(Object response) {
                            Toast.makeText(PasswordReset.this, "An e-mail has been sent", Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(PasswordReset.this, Login.class);
                            startActivity(loginIntent);
                        }
                    },
                    new BandUpErrorListener() {
                        @Override
                        public void onBandUpErrorResponse(VolleyError error) {
                            // show error
                        }
                    }
            );
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

}
