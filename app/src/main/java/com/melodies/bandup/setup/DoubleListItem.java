package com.melodies.bandup.setup;

/**
 * Created by Bergthor on 16.9.2016.
 */
public class DoubleListItem {
    public final String name;
    public final int drawableId;
    public Boolean isSelected;

    DoubleListItem (int drawableId, String name) {
        this.name = name;
        this.drawableId = drawableId;
        this.isSelected = false;
    }
}