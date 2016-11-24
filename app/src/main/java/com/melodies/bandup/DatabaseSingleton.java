package com.melodies.bandup;

import android.content.Context;

import com.melodies.bandup.repositories.BandUpDatabase;

public class DatabaseSingleton {

    static DatabaseSingleton mInstance;
    static Context mContext;

    public void setBandUpDatabase(BandUpDatabase bandUpDatabase) {
        this.bandUpDatabase = bandUpDatabase;

    }

    public BandUpDatabase getBandUpDatabase() {
        return bandUpDatabase;
    }

    private BandUpDatabase bandUpDatabase;

    DatabaseSingleton(Context context) {
        mContext = context;
    }



    public synchronized static DatabaseSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new DatabaseSingleton(context);
        }
        return mInstance;
    }
}
