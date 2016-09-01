package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // when Sign In is Clicked grab data and ...
    public void onClickSignIn(View v) {
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final Button btnSignIn = (Button) findViewById(R.id.btnSignIn);

        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();

        if (v.getId() == R.id.btnSignIn) {
            // username
            // password
            // ...do stuff
        }
    }

    // when Sign Up is Clicked go to Registration View
    public void onClickSignUp(View v) {
        final Button btnSignUp = (Button) findViewById(R.id.btnSignUp);

        if (v.getId() == R.id.btnSignUp) {
            Intent signUpIntent = new Intent(MainActivity.this, Register.class);
            MainActivity.this.startActivity(signUpIntent);
        }
    }
}