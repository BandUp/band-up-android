package com.melodies.bandup.setup;

/**
 * An item class that the DoubleListAdapter uses.
 */
public class DoubleListItem {
    public final String name;
    public final int drawableId;
    public Boolean isSelected;
    public final String id;

    DoubleListItem (String id, int drawableId, String name) {
        this.id = id;
        this.name = name;
        this.drawableId = drawableId;
        this.isSelected = false;
    }
}