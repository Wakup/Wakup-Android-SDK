package com.yellowpineapple.wakup.sdk.models;

/**
 * Representation of a Map Marker placed in a MapView categorized by the tags present in the offer
 */
public class MapMarker {

    String[] tags;
    int iconResId;

    /**
     * Constructor for Map Marker associated to given tags.
     * It will be used as representation in map for the offers that contains any of the selected tags.
     *
     * @param iconResId  Resource identifier for the Map Marker image
     * @param tags Array of offer tags that will be represented by the given icon
     */
    public MapMarker(int iconResId, String... tags) {
        this.tags = tags;
        this.iconResId = iconResId;
    }

    /**
     * Constructor for Map Marker without associated tags.
     * It will be used as default marker when no other applies.
     *
     * @param iconResId Resource identifier for the Image for the Map Marker
     */
    public MapMarker(int iconResId) {
        this(iconResId, (String[]) null);
    }

    public String[] getTags() {
        return tags;
    }

    public int getIconResId() {
        return iconResId;
    }

}
