package com.melodies.bandup.locale;

import android.content.Context;

/**
 * Grammar rules for English.
 */
public class LocaleRulesDefault implements LocaleRules {
    Context mContext;

    public LocaleRulesDefault(Context c) {
        mContext = c;
    }

    public Boolean ageIsPlural(int age) {
        return true;
    }
}
