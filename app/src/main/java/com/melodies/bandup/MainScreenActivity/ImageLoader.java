package com.melodies.bandup.MainScreenActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Bergthor on 16.10.2016.
 */

public class ImageLoader {

    Activity context;

    ImageLoader(Activity context) {
        this.context = context;
    }

    public void getProfilePhoto(String urlResponse, ImageView imageView, ProgressDialog pDialog) {
        getProfilePhoto(urlResponse, imageView);
        pDialog.dismiss();
    }

    String validateJSON(String json) {
        System.out.println("JSON");
        System.out.println(json);
        String imageURL = null;
        try {
            JSONObject urlObject = new JSONObject(json);
            if (!urlObject.isNull("url")) {
                imageURL = urlObject.getString("url");
            } else {
                Toast.makeText(context, "Could not parse JSON", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        if (imageURL == null || imageURL.equals("")) {
            return null;
        }
        return imageURL;
    }

    public void getProfilePhoto(String urlResponse, final ImageView imageView) {
        com.android.volley.toolbox.ImageLoader il = VolleySingleton.getInstance(context).getImageLoader();
        imageView.setImageResource(R.color.transparent);

        String imageUrl = validateJSON(urlResponse);
        if (imageUrl != null) {
            il.get(imageUrl, new com.android.volley.toolbox.ImageLoader.ImageListener() {
                @Override
                public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
                    final Bitmap b = response.getBitmap();
                    if (b != null) {
                        Runnable r = new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(b);
                            }
                        };
                        if (context != null) {
                            context.runOnUiThread(r);
                        }
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleySingleton.getInstance(context).checkCauseOfError(error);
                }
            });
        }
    }
}
