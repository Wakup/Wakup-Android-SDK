package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.CompanyDetail;

public class CompanyListView extends FrameLayout {

    public interface Listener {

        void onCompanySelected(CompanyDetail company);

    }

    private CompanyDetail company;
    private Listener listener = null;
    private boolean selected = false;

    /* Views */
    private RemoteImageView imageView;
    private View selectedView;

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
        inflate(getContext(), R.layout.wk_list_item_company, this);
        imageView = findViewById(R.id.imageView);
        selectedView = findViewById(R.id.selectedView);
        View clickableView = findViewById(R.id.clickableView);
        clickableView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCompanySelected(selected ? null : company);
                }
            }
        });
    }

    public void setCompany(CompanyDetail company) {
        if (company != this.company) {
            this.company = company;
            if (company != null) {
                imageView.setImage(company.getLogo());
            } else {
                imageView.setImage(null);
            }
        }
    }

    public CompanyDetail getCompany() {
        return company;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
        selectedView.setVisibility(selected ? VISIBLE : GONE);
        imageView.setAlpha(selected ? 1f : 0.3f);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
