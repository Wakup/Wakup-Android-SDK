package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Company;

public class CompanyListView extends FrameLayout {

    public interface Listener {

        void onCompanySelected(Company company);

    }

    private Company company;
    private Listener listener = null;
    private boolean selected = false;

    /* Views */
    private TextView textView;
    private View clickableView;

    public CompanyListView(Context context) {
        super(context);
        init(null, 0);
    }

    public CompanyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CompanyListView(Context context, AttributeSet attrs, int defStyle) {
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
                    listener.onCompanySelected(selected ? null : company);
                }
                setSelected(!selected);
            }
        });
    }

    public void setCompany(Company company) {
        this.company = company;
        if (company != null) {
            textView.setText(company.getName());
        } else {
            textView.setText(null);
        }
    }

    public Company getCompany() {
        return company;
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
