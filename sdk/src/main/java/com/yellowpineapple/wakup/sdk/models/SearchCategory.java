package com.yellowpineapple.wakup.sdk.models;

import com.yellowpineapple.wakup.sdk.R;

/**
 * Representation of an offer category used to filter the search results
 */
public class SearchCategory {

    private String[] tags;
    private int nameResId;
    private int iconResId;
    private int iconColorResId;

    /**
     * Creates a representation of an offer category that will be offered to the user to filter
     * search results using the selected tags. This category will use the default color.
     *
     * @param nameResId Name that will be displayed in filter button
     * @param iconResId Icon that will be displayed in filter button
     * @param tags Array of tags that will be allowed for results when the filter is applied
     */
    public SearchCategory(int nameResId, int iconResId, String... tags) {
        this(nameResId, iconResId, R.color.wk_action_active, tags);
    }

    /**
     * Creates a representation of an offer category with custom color that will be offered to the
     * user to filter search results using the selected tags
     *
     * @param nameResId Name that will be displayed in filter button
     * @param iconResId Icon that will be displayed in filter button
     * @param iconColorResId Color for de icon
     * @param tags Array of tags that will be allowed for results when the filter is applied
     */
    public SearchCategory(int nameResId, int iconResId, int iconColorResId, String... tags) {
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
