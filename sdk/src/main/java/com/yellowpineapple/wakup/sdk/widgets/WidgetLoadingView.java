package com.yellowpineapple.wakup.sdk.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.ImageOptions;
import com.yellowpineapple.wakup.sdk.views.OfferCarouselItemView;
import com.yellowpineapple.wakup.sdk.views.OfferSmallView;

import java.util.List;

/**
 * Created by agutierrez on 3/11/16.
 */
public class WidgetLoadingView extends FrameLayout {

    // Views
    TextView txtTitle;
    TextView txtMessage;
    View progressBar;
    View errorView;


    public WidgetLoadingView(Context context) {
        super(context);
        init();
    }

    public WidgetLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WidgetLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.wk_widget_loading, this);
        progressBar = findViewById(R.id.progressBar);
        errorView = findViewById(R.id.errorView);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtTitle.setText(null);
        txtMessage.setText(null);
    }

    public void setLoading(boolean loading) {
        setLoading(loading, true);
    }

    public void setLoading(boolean loading, boolean animated) {
        animateVisibility(progressBar, loading, animated);
        animateVisibility(errorView, !loading, animated);
    }

    private void animateVisibility(final View view, boolean visible, boolean animated) {
        int duration = animated ? 300 : 0;
        if (visible) {
            if (view.getVisibility() != VISIBLE) {
                view.setAlpha(0f);
                view.setVisibility(VISIBLE);
                view.animate().alpha(1f).setDuration(duration).setListener(null);
            }
        } else {
            if (view.getVisibility() == VISIBLE) {
                view.animate().alpha(0f).setDuration(duration).
                        setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                view.setVisibility(GONE);
                            }
                        });
            }
        }
    }

    public void setVisible(boolean visible) {
        setVisible(visible, true);
    }

    public void setVisible(boolean visible, boolean animated) {
        animateVisibility(this, visible, animated);
    }

    public void showError(String message) {
        showError(getContext().getString(R.string.wk_widget_default_error_title), message);
    }

    public void showError(String title, String message) {
        txtTitle.setText(title);
        txtMessage.setText(message);
        setLoading(false);
    }
}
