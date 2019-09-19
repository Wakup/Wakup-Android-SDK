package com.yellowpineapple.wakup.sdk.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yellowpineapple.wakup.sdk.R;

/**
 * Created by agutierrez on 14/11/16.
 */

public class Widget extends LinearLayout {

    protected WidgetLoadingView loadingView;
    protected View retryRippleView;

    protected OnRetryListener onRetryListener = null;

    public interface OnRetryListener {
        void onRetry();
    }

    public Widget(Context context) {
        super(context);
    }

    public Widget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Widget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    void afterViews() {
        loadingView = (WidgetLoadingView) findViewById(R.id.loadingView);
        if (loadingView != null) {
            loadingView.setLoading(true, false);
            loadingView.setVisible(true);
        }
        retryRippleView = findViewById(R.id.retryRippleView);
        if (retryRippleView != null) {
            retryRippleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    triggerOnRetry();
                }
            });
        }
    }

    public void setLoading(boolean loading) {
        if (loadingView != null) {
            loadingView.setVisible(loading);
            loadingView.setLoading(loading);
        }
    }

    public void displayError(String errorMessage) {
        displayError(getContext().getString(R.string.wk_widget_default_error_title), errorMessage);
    }

    public void displayError(String errorTitle, String errorMessage) {
        loadingView.showError(errorTitle, errorMessage);
        loadingView.setVisible(true);
    }

    public void displayLocationError() {
        displayError(getContext().getString(R.string.wk_widget_location_error_title), getContext().getString(R.string.wk_widget_location_error_message));
    }

    public void setOnRetryListener(OnRetryListener onRetryListener) {
        this.onRetryListener = onRetryListener;
    }

    protected void triggerOnRetry() {
        if (onRetryListener != null) onRetryListener.onRetry();
    }
}
