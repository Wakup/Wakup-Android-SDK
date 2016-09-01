package com.yellowpineapple.wakup.sdk.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RedemptionCodeDetail;
import com.yellowpineapple.wakup.sdk.models.RemoteImage;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.views.RemoteImageView;

public class OfferCouponsActivity extends Activity {

    public final static String CODE_EXTRA = "code";
    public final static String OFFER_EXTRA = "offer";

    // Properties
    Offer offer;
    RedemptionCodeDetail redemptionCodeDetail;

    // Views
    CarouselView carouselView;
    TextView txtCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wk_activity_coupons);

        injectExtras();
        injectViews();
    }

    private void injectExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            if (extras.containsKey(OFFER_EXTRA))
                offer = (Offer) extras.getSerializable(OFFER_EXTRA);
            if (extras.containsKey(CODE_EXTRA))
                redemptionCodeDetail = (RedemptionCodeDetail) extras.getSerializable(CODE_EXTRA);
        }
    }

    private void injectViews() {
        txtCode = (TextView) findViewById(R.id.txtCode);
        carouselView = (CarouselView) findViewById(R.id.carouselView);

        if (redemptionCodeDetail != null) {
            txtCode.setText(redemptionCodeDetail.getDisplayCode());
            carouselView.setViewListener(viewListener);
            carouselView.setPageCount(redemptionCodeDetail.getFormats().size());
        }

        View.OnClickListener closeOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };

        View contentView = findViewById(R.id.content_view);
        contentView.setOnClickListener(closeOnClickListener);
        View mainView = findViewById(R.id.main_view);
        mainView.setOnClickListener(closeOnClickListener);
    }

    RequestClient getRequestClient() {
        return RequestClient.getSharedInstance(this);
    }

    public static Builder intent(Context context, Offer offer, RedemptionCodeDetail redemptionCodeDetail) {
        return new Builder(context, offer, redemptionCodeDetail);
    }

    public static class Builder extends IntentBuilder<OfferCouponsActivity> {

        public Builder(Context context, Offer offer, RedemptionCodeDetail redemptionCodeDetail) {
            super(OfferCouponsActivity.class, context);
            getIntent().putExtra(CODE_EXTRA, redemptionCodeDetail);
            getIntent().putExtra(OFFER_EXTRA, offer);
        }
    }

    ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(final int position) {
            String format = redemptionCodeDetail.getFormats().get(position);
            RemoteImage image = getRequestClient().getCouponImage(offer, format);
            RemoteImageView imageView = new RemoteImageView(OfferCouponsActivity.this);
            int paddingBottom = getResources().getDimensionPixelSize(R.dimen.wk_coupon_padding);
            imageView.setPadding(0, 0, 0, paddingBottom);
            imageView.setImage(image);
            return imageView;
        }
    };
}
