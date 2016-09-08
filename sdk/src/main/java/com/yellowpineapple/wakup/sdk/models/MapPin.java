package com.yellowpineapple.wakup.sdk.models;

/**
 * Representation of a PIN placed in a MapView categorized by the tags present in the offer
 */
public class MapPin {

    String[] tags;
    int iconResId;

    public MapPin(int iconResId, String... tags) {
        this.tags = tags;
        this.iconResId = iconResId;
    }

    /**
     * Constructor for Pin without associated tags.
     * It will be applied to all offers regardless of its tags.
     *
     * @param iconResId Resource identifier for the Image for the Map Pin
     */
    public MapPin(int iconResId) {
        this(iconResId, (String[]) null);
    }

    public String[] getTags() {
        return tags;
    }

    public int getIconResId() {
        return iconResId;
    }

}
