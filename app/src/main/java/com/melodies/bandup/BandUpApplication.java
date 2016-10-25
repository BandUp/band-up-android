package com.melodies.bandup;

import android.app.Application;
import android.content.res.Configuration;

import com.melodies.bandup.repositories.BandUpDatabase;
import com.melodies.bandup.repositories.BandUpRepository;

public class BandUpApplication extends Application {
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setRepository(BandUpDatabase database) {
        DatabaseSingleton.getInstance(getApplicationContext()).setBandUpDatabase(database);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setRepository(new BandUpRepository(this));
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
