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
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserListTest {

    @Rule
    public ActivityTestRule<MainScreenActivity> mActivityRule = new ActivityTestRule<>(
            MainScreenActivity.class, true, false);

    private BandUpMockRepository getMockWithItems() {


        return new BandUpMockRepository() {

            @Override
            public void getUserList(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONArray arr = null;
                JSONArray insArr = new JSONArray();
                insArr.put("ASDF");
                insArr.put("FDSA");
                JSONArray genArr = new JSONArray();
                genArr.put("QWERTY");
                genArr.put("YTREWQ");
                try {
                    arr = new JSONArray();
                    JSONObject obj = new JSONObject();
                    obj.put("_id", "di");
                    obj.put("username", "TestUser");
                    obj.put("status", "Looking for a band");
                    obj.put("instruments", insArr);
                    obj.put("genres", genArr);
                    obj.put("distance", 5.4);
                    obj.put("percentage", 55);
                    obj.put("image", null);
                    obj.put("dateOfBirth", "1997-11-08T20:44:36.000Z");
                    obj.put("aboutme", "I AM ZE BEST");
                    arr.put(obj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                responseListener.onBandUpResponse(arr);
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
        app.setRepository(getMockWithItems());
        app.setLocale(new LocaleRulesDefault(app));

        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.user_list_no_users))).check(matches(not(isDisplayed())));

        onView(withId(R.id.txtName))          .check(matches(withText("TestUser")));
        onView(withId(R.id.txtPercentage))    .check(matches(withText("55%")));
        onView(withId(R.id.txtDistance))      .check(matches(withText("5 " + mActivityRule.getActivity().getResources().getString(R.string.km_distance))));
        onView(withId(R.id.txtGenres))        .check(matches(withText("QWERTY")));
        onView(withId(R.id.txtMainInstrument)).check(matches(withText("ASDF")));
    }
}
