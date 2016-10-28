package com.melodies.bandup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.LruCache;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.melodies.bandup.repositories.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.charset.StandardCharsets;

/**
 * Created by Dagur on 3.9.2016.
 */
public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader; // to get user pictures
    private static Context mCtx;

    private VolleySingleton(Context context){
        mCtx = context;
        mRequestQueue = getRequestQueue();

        CookieManager cookieManager = new CookieManager(new PersistentCookieStore(mCtx), CookiePolicy.ACCEPT_ALL);
        CookieHandler.setDefault(cookieManager);

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache(){
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);
            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized VolleySingleton getInstance(Context context){
        if (mInstance == null){
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    /**
     * This function checks the cause of the Volley Error and prints out a relevant Toast message.
     *
     * @param error   The Volley Error object
     */
    public void checkCauseOfError(VolleyError error) {
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Toast.makeText(mCtx, "Connection error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof AuthFailureError) {
            mCtx.startActivity(new Intent(mCtx, Login.class));
        }
        else if (error instanceof ServerError) {
            String jsonString = new String(error.networkResponse.data, StandardCharsets.UTF_8);

            try {
                JSONObject myObject = new JSONObject(jsonString);
                int errNo      = myObject.getInt("err");
                String message = myObject.getString("msg");
                Toast.makeText(mCtx, message, Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                Toast.makeText(mCtx, "Server error!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
        else if (error instanceof NetworkError) {
            Toast.makeText(mCtx, "Network error!", Toast.LENGTH_LONG).show();
        }
        else if (error instanceof ParseError) {
            Toast.makeText(mCtx, "Server parse error!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(mCtx, "Unknown error! Contact Administrator", Toast.LENGTH_LONG).show();
        }
    }

}
