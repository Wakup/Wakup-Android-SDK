package com.yellowpineapple.wakup.sdk.models;

/**
 * Created by agutierrez on 10/02/15.
 */
public class Category {

    String[] tags;
    int nameResId;
    int iconResId;
    int iconColorResId;

    public Category(int nameResId, int iconResId, int iconColorResId, String... tags) {
        this.tags = tags;
        this.nameResId = nameResId;
        this.iconResId = iconResId;
        this.iconColorResId = iconColorResId;
    }

    public String[] getTags() {
        return tags;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getNameResId() {
        return nameResId;
    }

    public int getIconColorResId() {
        return iconColorResId;
    }
}
