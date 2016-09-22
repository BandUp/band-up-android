package com.melodies.bandup.setup;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonRequest;
import com.melodies.bandup.R;
import com.melodies.bandup.VolleySingleton;

import org.json.JSONArray;

public class SetupShared {

        public void onItemClick(Context context, AdapterView<?> parent, View view, int position, long id) {
        DoubleListItem inst = (DoubleListItem)parent.getAdapter().getItem(position);
        ImageView itemSelected = (ImageView) view.findViewById(R.id.itemSelected);

        // TODO: Find a better solution
        if (itemSelected.getVisibility() == view.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.shrink);
            itemSelected.startAnimation(animation);
            itemSelected.setVisibility(view.INVISIBLE);
            inst.isSelected = false;
        }
        else {
            itemSelected.setVisibility(view.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pop);
            itemSelected.startAnimation(animation);
            inst.isSelected = true;
        }
    }

    public void postSelectedItems(DoubleListAdapter dla, SetupListeners sl, Context c, String url) {
        JSONArray selectedItems = new JSONArray();

        for (DoubleListItem dli:dla.getDoubleList()) {
            if (dli.isSelected) {
                selectedItems.put(dli.id);
            }
        }

        JsonArrayRequest postItems = new JsonArrayRequest(
                Request.Method.POST,
                url,
                selectedItems,
                sl.getPickListener(),
                sl.getErrorListener()
        );

        VolleySingleton.getInstance(c).addToRequestQueue(postItems);
    }
}
