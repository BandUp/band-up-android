package com.melodies.bandup;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.melodies.bandup.MainScreenActivity.MainScreenActivity;
import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.locale.LocaleRulesDefault;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserListTest {

    @Rule
    public ActivityTestRule<MainScreenActivity> mActivityRule = new ActivityTestRule<>(
            MainScreenActivity.class, true, false);

    private BandUpMockRepository getMockWithItems(final int users) {
        return new BandUpMockRepository() {

            private JSONObject getUser(int id) {
                JSONArray insArr = new JSONArray();
                insArr.put("ASDF" + id);
                insArr.put("FDSA" + id);
                JSONArray genArr = new JSONArray();
                genArr.put("QWERTY" + id);
                genArr.put("YTREWQ" + id);
                JSONObject obj = new JSONObject();
                try {

                    obj.put("_id", "myId-" + id);
                    obj.put("username", "TestUser" + id);
                    obj.put("status", "Looking for a band + id");
                    obj.put("instruments", insArr);
                    obj.put("genres", genArr);
                    obj.put("distance", 5.4 * (id + 1));
                    obj.put("percentage", 10 + id + 1);
                    obj.put("image", null);
                    obj.put("dateOfBirth", "1997-11-08T20:44:36.000Z");
                    obj.put("aboutme", "I AM ZE BEST" + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return obj;
            }
            @Override
            public void getUserList(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONArray arr = new JSONArray();
                for (int i = 0; i < users; i++) {
                    arr.put(getUser(i));
                }
                responseListener.onBandUpResponse(arr);
            }

            @Override
            public void getUserProfile(JSONObject user, BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

                try {
                    if (user.getString("userId").equals("myId-0")) {
                        responseListener.onBandUpResponse(getUser(0));
                    } else if (user.getString("userId").equals("myId-1")) {
                        responseListener.onBandUpResponse(getUser(1));
                    }
                } catch (JSONException e) {
                    responseListener.onBandUpResponse("");
                }
            }
        };
    }

    private BandUpMockRepository getMockWithNoItems() {
        return new BandUpMockRepository() {
            @Override
            public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONArray arr = new JSONArray();
                responseListener.onBandUpResponse(arr);
            }
        };
    }

    @Test
    public void checkNoUsersNearby() {
        BandUpApplication app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithNoItems());

        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.user_list_no_users))).check(matches(isDisplayed()));
    }

    @Test
    public void checkUsersNearby() {
        BandUpApplication app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems(1));
        app.setLocale(new LocaleRulesDefault(app));

        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.user_list_no_users))).check(matches(not(isDisplayed())));

        onView(withId(R.id.txtName))          .check(matches(withText("TestUser0")));
        onView(withId(R.id.txtPercentage))    .check(matches(withText("11%")));
        onView(withId(R.id.txtGenres))        .check(matches(withText("QWERTY0")));
        onView(withId(R.id.txtMainInstrument)).check(matches(withText("ASDF0")));
    }

    @Test
    public void checkSingleUserDetails() {
        BandUpApplication app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems(1));
        app.setLocale(new LocaleRulesDefault(app));

        mActivityRule.launchActivity(new Intent());
        System.out.println();
        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.user_list_no_users))).check(matches(not(isDisplayed())));
        System.out.println();
        onView(withId(R.id.btnDetails)).perform(click());
        System.out.println();
        onView(withId(R.id.txtName))           .check(matches(withText("TestUser0")));
        onView(withId(R.id.txtPercentage))     .check(matches(withText("11%")));
        onView(withId(R.id.txtGenresList))     .check(matches(withText("QWERTY0\nYTREWQ0\n")));
        onView(withId(R.id.txtInstrumentsList)).check(matches(withText("ASDF0\nFDSA0\n")));

        pressBack();

        onView(withId(R.id.btnDetails)).perform(click());

        onView(withId(R.id.txtName))           .check(matches(withText("TestUser0")));
        onView(withId(R.id.txtPercentage))     .check(matches(withText("11%")));
        onView(withId(R.id.txtGenresList))     .check(matches(withText("QWERTY0\nYTREWQ0\n")));
        onView(withId(R.id.txtInstrumentsList)).check(matches(withText("ASDF0\nFDSA0\n")));




    }

    @Test
    public void checkMultipleUserDetailsAndOrder() {
        BandUpApplication app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems(2));
        app.setLocale(new LocaleRulesDefault(app));

        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.user_list_no_users))).check(matches(not(isDisplayed())));

        // Tap on the button that is inside the view that contains the username TestUser1
        onView(allOf(withId(R.id.btnDetails), withParent(hasSibling(allOf(withId(R.id.user_top_row), withChild(withText("TestUser1"))))))).perform(click());

        onView(withId(R.id.txtName))           .check(matches(withText("TestUser1")));
        onView(withId(R.id.txtPercentage))     .check(matches(withText("12%")));
        onView(withId(R.id.txtGenresList))     .check(matches(withText("QWERTY1\nYTREWQ1\n")));
        onView(withId(R.id.txtInstrumentsList)).check(matches(withText("ASDF1\nFDSA1\n")));

        pressBack();

        onView(withId(R.id.pager)).perform(swipeLeft());

        // Tap on the button that is inside the view that contains the username TestUser0
        onView(allOf(withId(R.id.btnDetails), withParent(hasSibling(allOf(withId(R.id.user_top_row), withChild(withText("TestUser0"))))))).perform(click());

        onView(withId(R.id.txtName))           .check(matches(withText("TestUser0")));
        onView(withId(R.id.txtPercentage))     .check(matches(withText("11%")));
        onView(withId(R.id.txtGenresList))     .check(matches(withText("QWERTY0\nYTREWQ0\n")));
        onView(withId(R.id.txtInstrumentsList)).check(matches(withText("ASDF0\nFDSA0\n")));


    }
}
