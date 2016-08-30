package com.yellowpineapple.wakup.sdk.models;

import com.yellowpineapple.wakup.sdk.R;

/**
 * Created by agutierrez on 10/02/15.
 */
public enum Category {

    UNKNOWN(null, R.string.wk_category_unknown, R.drawable.wk_pin_unknown),
    LEISURE(new String[]{"leisure"}, R.string.wk_category_leisure, R.drawable.wk_pin_leisure),
    RESTAURANTS(new String[]{"restaurants"}, R.string.wk_category_restaurants, R.drawable.wk_pin_restaurants),
    SERVICES(new String[]{"services"}, R.string.wk_category_services, R.drawable.wk_pin_services),
    SHOPPING(new String[]{"shopping"}, R.string.wk_category_shopping, R.drawable.wk_pin_shopping);

    String[] tags;
    int nameResId;
    int iconResId;

    Category(String[] tags, int nameResId, int iconResId) {
        this.tags = tags;
        this.nameResId = nameResId;
        this.iconResId = iconResId;
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
}
