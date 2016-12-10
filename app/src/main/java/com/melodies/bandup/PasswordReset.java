package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

    TextInputLayout tilEmail;
    EditText etEmail;

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tilEmail = (TextInputLayout) findViewById(R.id.til_email_reset);
        etEmail = (EditText) findViewById(R.id.txt_email_reset);
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * create a request for password reset
     * notify user to check e-mail if succesfull
     * otherwise show an apropriate notification
     *
     * @param v
     */
    public void onSendPasswordReset(View v){

        if (isValidEmail(etEmail.getText().toString())) {
            tilEmail.setErrorEnabled(false);
            try {
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            tilEmail.setError(getString(R.string.register_til_error_email_format));
        }
    }
    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }
}
