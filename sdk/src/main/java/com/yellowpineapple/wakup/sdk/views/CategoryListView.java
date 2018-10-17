package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Category;

public class CategoryListView extends FrameLayout {

    public interface Listener {

        void onCategorySelected(Category category);

    }

    private Category category;
    private Listener listener = null;
    private boolean selected = false;

    /* Views */
    private TextView textView;
    private View clickableView;

    public CategoryListView(Context context) {
        super(context);
        init(null, 0);
    }

    public CategoryListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CategoryListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        injectViews();
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_list_item_category, this);
        textView = findViewById(R.id.textView);
        clickableView = findViewById(R.id.clickableView);
        clickableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCategorySelected(selected ? null : category);
                }
                setSelected(!selected);
            }
        });
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            textView.setText(category.getName());
        } else {
            textView.setText(null);
        }
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        clickableView.setSelected(selected);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
