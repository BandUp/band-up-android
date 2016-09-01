package com.melodies.bandup;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest extends AppCompatActivity {

    private EditText password1, password2;

    @Test
    public void notEmptyPassword() throws Exception {
        assertNotNull(password1);
    }

    @Test // check if password is the same
    public void samePassword() throws Exception {
        password1 = (EditText) findViewById(R.id.etPassword);
        password2 = (EditText) findViewById(R.id.etPassword2);
        String pass1 = password1.getText().toString();
        String pass2 = password2.getText().toString();
        assertEquals(pass1, pass1.equals(pass2));
    }
}


