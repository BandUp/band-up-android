package com.melodies.bandup;

import android.content.Context;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SetupTest {

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

    @Test
    public void checkInstruments() {
        BandUpMockRepository mockRepository = new BandUpMockRepository() {
            @Override
            public void local_login(Context context, JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("sessionID", "ASDF");
                    obj.put("hasFinishedSetup", false);
                    obj.put("userID", "ASDFASDF");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(obj);
            }

            @Override
            public void getInstruments(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

                JSONArray arr = null;
                try {
                    arr = new JSONArray();
                    JSONObject obj = new JSONObject();
                    obj.put("_id", "String");
                    obj.put("order", 1);
                    obj.put("name", "Bongo Drums");
                    arr.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(arr);
            }
        };

        app.setRepository(mockRepository);


        onView(withId(R.id.etUsername)).perform(typeText(mUsernameToBetyped), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(mPasswordToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());

        onView(withText("Bongo Drums")).check(matches(isDisplayed()));
    }

    @Test
    public void checkEmptyInstruments() {
        BandUpMockRepository mockRepository = new BandUpMockRepository() {
            @Override
            public void local_login(Context context, JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("sessionID", "ASDF");
                    obj.put("hasFinishedSetup", false);
                    obj.put("userID", "ASDFASDF");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(obj);
            }

            @Override
            public void getInstruments(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

                JSONArray arr = new JSONArray();
                responseListener.onBandUpResponse(arr);
            }
        };

        app.setRepository(mockRepository);


        onView(withId(R.id.etUsername)).perform(typeText(mUsernameToBetyped), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(mPasswordToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.setup_no_instruments))).check(matches(isDisplayed()));
    }

    @Test
    public void checkNoSelectionInstruments() {
        BandUpMockRepository mockRepository = new BandUpMockRepository() {
            @Override
            public void local_login(Context context, JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("sessionID", "ASDF");
                    obj.put("hasFinishedSetup", false);
                    obj.put("userID", "ASDFASDF");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(obj);
            }

            @Override
            public void getInstruments(Context context, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

                JSONArray arr = null;
                try {
                    arr = new JSONArray();
                    JSONObject obj = new JSONObject();
                    obj.put("_id", "String");
                    obj.put("order", 1);
                    obj.put("name", "Bongo Drums");
                    arr.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(arr);
            }
        };

        app.setRepository(mockRepository);


        onView(withId(R.id.etUsername)).perform(typeText(mUsernameToBetyped), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(mPasswordToBetyped), closeSoftKeyboard());

        onView(withId(R.id.btnSignIn)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(0)
                .perform(click());

        onView(withId(R.id.btnNext)).perform(click());
    }
}
