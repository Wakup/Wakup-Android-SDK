package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Category;

/**
 * Created by agutierrez on 7/9/16.
 */
public class SearchFilterButton extends OfferActionButton {

    Category category;

    public SearchFilterButton(Context context) {
        super(context);
        init();
    }

    public SearchFilterButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchFilterButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        setFontSize((float) getResources().getDimensionPixelSize(R.dimen.wk_category_button_text));
    }

    public void setCategory(Category category) {
        this.category = category;
        setText(category.getNameResId());
        setIcon(category.getIconResId(), category.getIconColorResId());
    }

    public Category getCategory() {
        return category;
    }
}
