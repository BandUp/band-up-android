package com.melodies.bandup.locale;

import android.content.Context;

/**
 * Grammar rules for English.
 */
public class LocaleRules_en implements LocaleRules {
    Context mContext;

    public LocaleRules_en(Context c) {
        mContext = c;
    }

    public Boolean ageIsPlural(int age) {
        return age != 1;
    }
}
