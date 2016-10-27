package com.melodies.bandup.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import com.melodies.bandup.R;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Created by dad on 27-Oct-16.
 */

public class PersistentCookieStore implements CookieStore, Runnable {
    private CookieStore store;
    private Context     mCtx;

    private final String PREFS_LOC = "Band_up_cookie_store";

    public PersistentCookieStore(){
        store = new CookieManager().getCookieStore();
        SharedPreferences sh = mCtx.getSharedPreferences(PREFS_LOC, Context.MODE_PRIVATE);

        // TODO: read cookie from persistent storage
        Map<String, ?> mCookies = sh.getAll();

        for (Map.Entry<String, ?> cookie : mCookies.entrySet()){
            try {
                this.add(new URI(mCtx.getResources().getString(R.string.api_address)),
                         new HttpCookie(cookie.getKey(), (String)cookie.getValue()));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }


        Runtime.getRuntime().addShutdownHook(new Thread(this));
    }

    @Override
    public void run() {
        // TODO: write all cookies to persistent store
        SharedPreferences sh = mCtx.getSharedPreferences(PREFS_LOC, Context.MODE_PRIVATE);
        SharedPreferences.Editor shEdit = sh.edit();
        List<HttpCookie> cookies = store.getCookies();

        for (int i = 0; i < cookies.size(); i++){
            HttpCookie cookie = cookies.get(i);
            String name = cookie.getName();
            String value = cookie.getValue();

            shEdit.putString(name, value);
            // apply offloads saving to another thread
            shEdit.apply();
        }
        // in case we are still waitng for last applies
        shEdit.commit();
    }

    @Override
    public void add(URI uri, HttpCookie httpCookie) {
        store.add(uri, httpCookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        return store.get(uri);
    }

    @Override
    public List<HttpCookie> getCookies() {
        return store.getCookies();
    }

    @Override
    public List<URI> getURIs() {
        return store.getURIs();
    }

    @Override
    public boolean remove(URI uri, HttpCookie httpCookie) {
        return store.remove(uri, httpCookie);
    }

    @Override
    public boolean removeAll() {
        return store.removeAll();
    }
}
