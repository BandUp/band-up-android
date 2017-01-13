package com.melodies.bandup.main_screen_activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.melodies.bandup.R;

public class Contact extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        setTitle(getString(R.string.contact_title));

        // back to activity before
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    // Return to previous Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
