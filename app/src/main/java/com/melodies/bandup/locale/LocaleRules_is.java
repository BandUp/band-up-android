package com.melodies.bandup.locale;

import android.content.Context;

/**
 * Created by Bergthor on 4.11.2016.
 */

/**
 * Grammar rules for Icelandic.
 */
public class LocaleRules_is implements LocaleRules {
    Context mContext;

    public LocaleRules_is(Context c) {
        mContext = c;
    }
    public Boolean ageIsPlural(int age) {
        return !((age % 10 == 1) && (age != 11));
    }
}
