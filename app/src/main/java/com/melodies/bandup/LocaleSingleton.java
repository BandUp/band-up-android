package com.melodies.bandup;

import android.content.Context;

import com.melodies.bandup.locale.LocaleRules;

public class LocaleSingleton {

    static LocaleSingleton mInstance;
    static Context mContext;

    public void setLocale(LocaleRules locale) {
        this.locale = locale;

    }

    public LocaleRules getLocaleRules() {
        return locale;
    }

    private LocaleRules locale;

    LocaleSingleton(Context context) {
        mContext = context;
    }



    public synchronized static LocaleSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new LocaleSingleton(context);
        }
        return mInstance;
    }
}
