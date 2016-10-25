package com.melodies.bandup;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginTest {

    private String mUsernameToBetyped;
    private String mPasswordToBetyped;
    BandUpApplication app;


    @Rule
    public ActivityTestRule<Login> mActivityRule = new ActivityTestRule<>(
            Login.class);

    @Before
    public void initValidString() {
        // Specify a valid string.
        mUsernameToBetyped = "aaa";
        mPasswordToBetyped = "aaa";

        app = (BandUpApplication) mActivityRule.getActivity().getApplication();
    }

    @Test
    public void checkUsernameInputLogin() {
        // Type text and then press the button.
        onView(withId(R.id.etUsername)).perform(typeText(mUsernameToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());

        onView(withId(R.id.tilPassword)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(mActivityRule.getActivity().getString(R.string.login_password_validation))));
    }

    @Test
    public void checkPasswordInputLogin() {
        // Type text and then press the button.
        onView(withId(R.id.etPassword)).perform(typeText(mPasswordToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());

        onView(withId(R.id.tilUsername)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(mActivityRule.getActivity().getString(R.string.login_username_validation))));
    }

    @Test
    public void checkNoInputLogin() {
        // Press the login button immediately.
        onView(withId(R.id.btnSignIn)).perform(click());

        // Username and password need to display an error message.
        onView(withId(R.id.tilUsername)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(mActivityRule.getActivity().getString(R.string.login_username_validation))));
        onView(withId(R.id.tilPassword)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(mActivityRule.getActivity().getString(R.string.login_password_validation))));

    }

}
