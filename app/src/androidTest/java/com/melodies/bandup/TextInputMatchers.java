package com.melodies.bandup;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public final class TextInputMatchers {

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     *
     * @param expectedHintText {@link Matcher} of {@link String} with text to match
     */
    @NonNull
    public static Matcher<View> hasTextInputLayoutHintText(final String expectedHintText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence hint = ((TextInputLayout) view).getHint();

                if (hint == null) {
                    return false;
                }

                String hintString = hint.toString();

                return expectedHintText.equals(hintString);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(expectedHintText);
            }
        };
    }

    /**
     * Returns a matcher that matches {@link TextView}s based on text property value.
     *
     * @param expectedErrorText {@link Matcher} of {@link String} with text to match
     */
    @NonNull
    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                String errorString = error.toString();

                return expectedErrorText.equals(errorString);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}