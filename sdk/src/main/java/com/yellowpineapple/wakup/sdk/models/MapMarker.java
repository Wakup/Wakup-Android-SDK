package com.yellowpineapple.wakup.sdk.models;

import android.content.Context;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Representation of a Map Marker placed in a MapView categorized by the tags present in the offer
 */
public class MapMarker {

    private String[] tags;
    private int iconResId;

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

    public static int getOfferIcon(Context context, Offer offer) {
        List<MapMarker> mapPinCategories = PersistenceHandler.getSharedInstance(context).getOptions().getMapMarkers();
        // Set default value
        int offerIcon = R.drawable.wk_ic_pin_unknown;
        if (mapPinCategories != null && mapPinCategories.size() > 0) {
            List<String> offerTags = offer.getTags();
            if (offerTags != null && offerTags.size() > 0) {
                for (MapMarker category: mapPinCategories) {
                    if (category.getTags() != null && category.getTags().length > 0) {
                        List<String> categoryTags = Arrays.asList(category.getTags());
                        // Disjoint will return true when collections have no elements in common
                        if (!Collections.disjoint(categoryTags, offerTags)) {
                            offerIcon = category.getIconResId();
                            break;
                        }
                    } else {
                        offerIcon = category.getIconResId();
                    }
                }
            }
        }
        return offerIcon;
    }

}
