package com.melodies.bandup.repositories;

import com.melodies.bandup.listeners.BandUpErrorListener;
import com.melodies.bandup.listeners.BandUpResponseListener;

import org.json.JSONArray;
import org.json.JSONObject;

public interface BandUpDatabase {

    void local_login(JSONObject user,
                     BandUpResponseListener responseListener,
                     BandUpErrorListener errorListener);


    void getUserProfile(final JSONObject user,
                        final BandUpResponseListener responseListener,
                        final BandUpErrorListener errorListener);

    void getInstruments(final BandUpResponseListener responseListener,
                        final BandUpErrorListener errorListener);

    void getGenres(final BandUpResponseListener setupItemsListener,
                   final BandUpErrorListener setupItemsErrorListener);

    void postInstruments(final JSONArray instruments,
                         final BandUpResponseListener responseListener,
                         final BandUpErrorListener errorListener);

    void postGenres(final JSONArray genres,
                    final BandUpResponseListener responseListener,
                    final BandUpErrorListener errorListener);

    void getFilter(final BandUpResponseListener setupItemsListener,
                   final BandUpErrorListener setupItemsErrorListener);

    void getUserList(final BandUpResponseListener setupItemsListener,
                     final BandUpErrorListener setupItemsErrorListener);

    void sendGCMRegToken(final JSONObject tokenObject,
                         final BandUpResponseListener responseListener,
                         final BandUpErrorListener errorListener);

    void postLike(final JSONObject userObject,
                  final BandUpResponseListener responseListener,
                  final BandUpErrorListener errorListener);

    void  postLocation(JSONObject locationObject,
                       BandUpResponseListener responseListener,
                       BandUpErrorListener errorListener);

    void isLoggedIn(BandUpResponseListener responseListener,
                    BandUpErrorListener errorListener);


    void logout(BandUpResponseListener responseListener,
                BandUpErrorListener errorListener);

    void updateUser(final JSONObject updatedObject,
                    final BandUpResponseListener responseListener,
                    final BandUpErrorListener errorListener);

    void sendSoundCloudId(JSONObject requestObject,
                          BandUpResponseListener responseListener,
                          BandUpErrorListener errorListener);

    void sendSoundCloudUrl(JSONObject requestObject,
                           BandUpResponseListener responseListener,
                           BandUpErrorListener errorListener);

    void getEmailInUse(JSONObject requestObject,
                           BandUpResponseListener responseListener,
                           BandUpErrorListener errorListener);

    void register(JSONObject requestObject,
                  BandUpResponseListener responseListener,
                  BandUpErrorListener errorListener);

    void getSearchQuery(JSONObject searchObject,
                        BandUpResponseListener responseListener,
                        BandUpErrorListener errorListener);

    void sendPasswordResetRequest(JSONObject requestObject,
                                  BandUpResponseListener responseListener,
                                  BandUpErrorListener errorListener);

    void delete_user(JSONObject requestObject,
                     BandUpResponseListener responseListener,
                     BandUpErrorListener errorListener);
}

