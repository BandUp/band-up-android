package com.melodies.bandup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Register extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    public void onClickRegister(View v) {
        // binding vire to variables
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
        final EditText etAge = (EditText) findViewById(R.id.etAge);
        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        // converting to string
        final String email = etEmail.getText().toString();
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String password2 = etPassword2.getText().toString();
        final String age = etAge.getText().toString();

        // when button Register is pushed:
        if (v.getId() == R.id.btnRegister) {
            // check if passwords match
            if (!password.equals(password2)) {
                // popup msg
                Toast pass = Toast.makeText(Register.this, "Passwords don't match!", Toast.LENGTH_SHORT);
                pass.show();
            } else {
                // insert new user into Database and..

                //..go to Sign In view
                Intent registerIntent = new Intent(Register.this, MainActivity.class);
                Register.this.startActivity(registerIntent);

            }
        }

    }

}

