package com.melodies.bandup;

import android.content.Context;

/**
 * Created by Bergthor on 15.10.2016.
 */

public class CurrentUserSingleton {

    private static CurrentUserSingleton mInstance;

    CurrentUserSingleton(Context context) {

    }
    public static synchronized CurrentUserSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new CurrentUserSingleton(context);
        }
        return mInstance;
    }
}
