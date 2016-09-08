package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;

import java.util.ArrayList;
import java.util.List;

public class SearchFiltersView extends FrameLayout {

    List<SearchFilterButton> buttons = new ArrayList<>();

    public SearchFiltersView(Context context) {
        super(context);
        init(null, 0);
    }

    public SearchFiltersView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public SearchFiltersView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
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

        PersistenceHandler persistenceHandler = PersistenceHandler.getSharedInstance(getContext());
        List<Category> categories = persistenceHandler.getOptions().getCategories();
        for (Category category : categories) {
            SearchFilterButton button = new SearchFilterButton(getContext());
            button.setCategory(category);
            button.setOnClickListener(onClickListener);
            buttonsContainer.addView(button);
        }

        for (OfferActionButton button : buttons) {
            button.setOnClickListener(onClickListener);
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
