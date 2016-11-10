package com.melodies.bandup;

import android.app.Application;
import android.content.res.Configuration;

import com.melodies.bandup.locale.LocaleRules;
import com.melodies.bandup.locale.LocaleRulesDefault;
import com.melodies.bandup.locale.LocaleRules_en;
import com.melodies.bandup.locale.LocaleRules_is;
import com.melodies.bandup.repositories.BandUpDatabase;
import com.melodies.bandup.repositories.BandUpRepository;

import java.util.Locale;

public class BandUpApplication extends Application {

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Also change the locale at runtime.
        // User may have changed it while the app is running.
        setLocale(null);
    }

    public void setRepository(BandUpDatabase database) {
        DatabaseSingleton.getInstance(getApplicationContext()).setBandUpDatabase(database);
    }

    /**
     *
     * @param lr when null, the device locale is selected.
     */
    public void setLocale(LocaleRules lr) {
        LocaleSingleton singleton = LocaleSingleton.getInstance(getApplicationContext());
        String language = Locale.getDefault().getLanguage();
        //String country = Locale.getDefault().getCountry();
        if (lr == null) {
            switch (language) {
                case "is":
                    singleton.setLocale(new LocaleRules_is(this));
                    break;
                case "en":
                    singleton.setLocale(new LocaleRules_en(this));
                    break;
                default:
                    singleton.setLocale(new LocaleRulesDefault(this));
                    break;
            }
        } else {
            singleton.setLocale(lr);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        setRepository(new BandUpRepository(this));
        setLocale(null);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
