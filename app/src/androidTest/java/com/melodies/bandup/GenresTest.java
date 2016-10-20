package com.melodies.bandup;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Genres;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenresTest {

    @Rule
    public ActivityTestRule<Genres> mActivityRule = new ActivityTestRule<>(
            Genres.class, true, false);

    private BandUpMockRepository getMockWithItems() {
        final List<String> list = new ArrayList<>();
        list.add("Bongo Drums");
        list.add("Banjo");

        return new BandUpMockRepository() {

            @Override
            public void getGenres(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONArray arr = null;
                try {
                    arr = new JSONArray();
                    for (int i = 0; i < list.size(); i++) {
                        JSONObject obj = new JSONObject();
                        obj.put("_id", "String".concat(String.valueOf(i)));
                        obj.put("order", i);
                        obj.put("name", list.get(i));
                        arr.put(obj);
                    }
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
            public void getGenres(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {
                JSONArray arr = new JSONArray();
                responseListener.onBandUpResponse(arr);
            }
        };
    }

    @Test
    public void checkGenresCheckmarkNotShownOnOtherItems() {

        BandUpApplication  app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems());

        mActivityRule.launchActivity(new Intent());

        // Click on the first grid view item.
        onData(anything()).inAdapterView(withId(R.id.genreGridView))
                .atPosition(0)
                .perform(click());

        // Check if the checkmark is visible on the first item
        onData(anything()).inAdapterView(withId(R.id.genreGridView))
                .atPosition(0)
                .onChildView(withId(R.id.itemSelected))
                .check(matches(isDisplayed()));

        // Check if the checkmark is not visible on the second item
        onData(anything()).inAdapterView(withId(R.id.genreGridView))
                .atPosition(1)
                .onChildView(withId(R.id.itemSelected))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void checkGenresShownInRightOrder() {

        BandUpApplication  app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems());

        mActivityRule.launchActivity(new Intent());

        // Check if text is right on the first item.
        onData(anything()).inAdapterView(withId(R.id.genreGridView))
                .atPosition(0)
                .onChildView(withId(R.id.itemName))
                .check(matches(withText("Bongo Drums")));

        // Check if text is right on the second item.
        onData(anything()).inAdapterView(withId(R.id.genreGridView))
                .atPosition(1)
                .onChildView(withId(R.id.itemName))
                .check(matches(withText("Banjo")));
    }

    @Test
    public void checkNoGenresNotDisplayed() {
        BandUpApplication  app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems());

        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.txtNoGenres)).check(matches(not(isDisplayed())));
    }

    @Test
    public void checkEmptyGenres() {
        BandUpApplication  app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithNoItems());

        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.setup_no_genres))).check(matches(isDisplayed()));
    }

    @Test
    public void checkNoSelectionGenres() {
        BandUpApplication app = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        app.setRepository(getMockWithItems());

        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.btnFinish)).perform(click());
    }
}
