package com.melodies.bandup;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;
import com.melodies.bandup.setup.Instruments;

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
public class InstrumentsTest {

    Context app;

    @Rule
    public ActivityTestRule<Instruments> mActivityRule = new ActivityTestRule<>(
            Instruments.class, true, false);

    @Test
    public void checkInstruments() {
        final List<String> list = new ArrayList<>();
        list.add("Bongo Drums");
        list.add("Banjo");

        BandUpMockRepository mockRepository = new BandUpMockRepository() {

            @Override
            public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

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
        BandUpApplication  instrumentation = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        instrumentation.setRepository(mockRepository);

        mActivityRule.launchActivity(new Intent());

        // Check if text is right on the first item.
        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(0)
                .onChildView(withId(R.id.itemName))
                .check(matches(withText("Bongo Drums")));

        // Check if text is right on the second item.
        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(1)
                .onChildView(withId(R.id.itemName))
                .check(matches(withText("Banjo")));

        // Click on the first grid view item.
        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(0)
                .perform(click());

        // Check if the checkmark is visible on the first item
        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(0)
                .onChildView(withId(R.id.itemSelected))
                .check(matches(isDisplayed()));

        // Check if the checkmark is not visible on the second item
        onData(anything()).inAdapterView(withId(R.id.instrumentGridView))
                .atPosition(1)
                .onChildView(withId(R.id.itemSelected))
                .check(matches(not(isDisplayed())));
    }

    @Test
    public void checkEmptyInstruments() {
        BandUpMockRepository mockRepository = new BandUpMockRepository() {
            @Override
            public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

                JSONArray arr = new JSONArray();
                responseListener.onBandUpResponse(arr);
            }
        };

        BandUpApplication  instrumentation = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        instrumentation.setRepository(mockRepository);
        mActivityRule.launchActivity(new Intent());

        onView(withText(mActivityRule.getActivity().getResources().getString(R.string.setup_no_instruments))).check(matches(isDisplayed()));
    }

    @Test
    public void checkNoSelectionInstruments() {
        BandUpMockRepository mockRepository = new BandUpMockRepository() {
            @Override
            public void getInstruments(BandUpResponseListener responseListener, BandUpErrorListener errorListener) {

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

        BandUpApplication  instrumentation = (BandUpApplication) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        instrumentation.setRepository(mockRepository);
        mActivityRule.launchActivity(new Intent());

        onView(withId(R.id.btnNext)).perform(click());
    }
}
