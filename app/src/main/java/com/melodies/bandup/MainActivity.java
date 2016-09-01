package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickSignUp(View v) {
        final Button btnSignUp = (Button) findViewById(R.id.btnSignUp);

        if (v.getId() == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(MainActivity.this, Register.class);
            MainActivity.this.startActivity(signUpIntent);
        }
    }
}