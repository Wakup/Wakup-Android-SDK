package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;

/**
 * Created by agutierrez on 30/8/16.
 */
public class OfferCouponSummaryView extends RelativeLayout {

    public interface CouponSummaryListener {
        void onCouponSelected(Offer offer);
    }

    Offer offer;
    CouponSummaryListener listener = null;

    /* Views */
    TextView txtTitle;

    public OfferCouponSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public OfferCouponSummaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfferCouponSummaryView(Context context) {
        super(context);
        init();
    }

    public void init() {
        injectViews();
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_view_offer_code_summary, this);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onCouponSelected(offer);
            }
        });
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public void setListener(CouponSummaryListener listener) {
        this.listener = listener;
    }
}
