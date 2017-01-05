package com.melodies.bandup.MainScreenActivity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.melodies.bandup.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        setTitle(getString(R.string.settings_privacy_policy));

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        displayLicense();

    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    private void displayLicense() {
        TextView txtLicence = (TextView)findViewById(R.id.txtLicense);

        try {
            InputStream myInput = PrivacyPolicy.this.getAssets().open("license.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(myInput));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }

            txtLicence.setText(total.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
