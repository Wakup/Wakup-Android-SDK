package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Category;

import java.util.ArrayList;
import java.util.List;

public class SearchFiltersView extends FrameLayout {

    List<SearchFilterButton> buttons = new ArrayList<>();
    List<Category> categories;

    public SearchFiltersView(Context context, List<Category> categories) {
        super(context);
        this.categories = categories;
        injectViews();
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_view_search_filters, this);
        LinearLayout buttonsContainer = (LinearLayout) findViewById(R.id.buttonsContainer);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
            }
        };

        int filtersSize = getResources().getDimensionPixelSize(R.dimen.wk_category_button_size);
        int margin = getResources().getDimensionPixelSize(R.dimen.wk_category_button_margin);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(filtersSize, filtersSize);
        layoutParams.setMargins(0, margin, margin, margin);
        if (categories != null) {
            for (Category category : categories) {
                SearchFilterButton button = new SearchFilterButton(getContext());
                button.setCategory(category);
                button.setOnClickListener(onClickListener);
                buttonsContainer.addView(button, layoutParams);
                buttons.add(button);
            }
        }
    }

    public List<Category> getSelectedCategories() {
        List<Category> categories = new ArrayList<>();
        for (SearchFilterButton button : buttons) {
            if (button.isSelected()) categories.add(button.getCategory());
        }
        return categories;
    }
}
