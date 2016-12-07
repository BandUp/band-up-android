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
import static android.support.test.espresso.matcher.ViewMatchers.assertThat;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationTest {

    private String name, email, pass, random;

    BandUpApplication app;

    @Rule
    public ActivityTestRule<Register> registerRule = new ActivityTestRule<>(
            Register.class);

    @Before
    public void initValidString() {
        email = "user@bd.com";
        name = "user";
        pass = "secret";
        random = "foobar";
        app = (BandUpApplication) registerRule.getActivity().getApplication();
    }

    @Test
    public void emptyFieldTest() {
        onView(withId(R.id.btnRegister)).perform(click());
        onView(withId(R.id.tilEmail)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_email))));
    }

    @Test
    public void singleEmailValidationTest() {
        assertThat(Register.isValidEmail(email), is (true));
     }

    @Test
    public void singleEmailValidationTestTwo() {
        assertThat(Register.isValidEmail(random), is (false));
    }

    @Test
    public void singleEmailInputTest() {
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilUsername)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_username))));
    }

    @Test
    public void singleNameInputTest() {
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilEmail)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_email))));
    }

    @Test
    public void singlePasswordInputTest() {
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilEmail)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_email))));
    }

    @Test
    public void PasswordNoEmailInputTestTwo() {
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilEmail)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_email))));
    }

    @Test
    public void onePasswordInputTest() {
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilPassword2)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_password_again))));
    }

    @Test
    public void PasswordValidationTestOne() {
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.etPassword2)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilDateOfBirth)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_enter_dateofbirth))));
    }

    @Test
    public void PasswordValidationTestTwo() {
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.etPassword2)).perform(typeText(random), closeSoftKeyboard());
        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilPassword1)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_password_mismatch))));
    }

    @Test
    public void datePickerTest() {
        closeSoftKeyboard();
        onView(withId(R.id.etDateOfBirth)).perform(click());
        onView(withText(registerRule.getActivity().getString(android.R.string.ok))).perform(click());

        onView(withId(R.id.btnRegister)).perform(click());

        onView(withId(R.id.tilEmail)).check(matches(TextInputMatchers.hasTextInputLayoutErrorText(registerRule.getActivity().getString(R.string.register_til_error_fill_email))));
    }

    /*
    @Test
    public void userRegistrationTest() {
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etUsername)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.etPassword2)).perform(typeText(pass), closeSoftKeyboard());
        onView(withId(R.id.etDateOfBirth)).perform(swipeDown());
        onView(withText("OK")).perform(click());
        onView(withId(R.id.btnRegister)).perform(click());
    }
    */


}
