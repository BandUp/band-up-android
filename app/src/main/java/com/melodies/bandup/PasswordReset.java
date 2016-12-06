package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

public class PasswordReset extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
                            Toast.makeText(PasswordReset.this, R.string.password_reset_email_sent, Toast.LENGTH_LONG).show();
                            Intent loginIntent = new Intent(PasswordReset.this, Login.class);
                            startActivity(loginIntent);
                            finish();
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
    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
