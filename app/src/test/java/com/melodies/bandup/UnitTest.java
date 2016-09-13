package com.melodies.bandup;

import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class UnitTest extends AppCompatActivity {

    private EditText password1, password2;

    @Test
    public void notEmptyPassword() throws Exception {
        assertNotNull(password1);
    }
}



