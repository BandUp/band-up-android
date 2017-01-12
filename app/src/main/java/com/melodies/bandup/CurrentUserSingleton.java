package com.melodies.bandup;

import android.content.Context;

public class CurrentUserSingleton {

    private static CurrentUserSingleton mInstance;
    private Context mContext;

    private CurrentUserSingleton(Context context) {
        mContext = context;
    }
    public static synchronized CurrentUserSingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new CurrentUserSingleton(context);
        }
        return mInstance;
    }
}
