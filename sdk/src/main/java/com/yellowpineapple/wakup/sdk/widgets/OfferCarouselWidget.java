package com.yellowpineapple.wakup.sdk.widgets;

import android.content.Context;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.ImageOptions;
import com.yellowpineapple.wakup.sdk.views.OfferCarouselItemView;

import java.util.List;

/**
 * Created by agutierrez on 3/11/16.
 */
public class OfferCarouselWidget extends LinearLayout {

    Location location;

    // Views
    CarouselView carouselView;


    public OfferCarouselWidget(Context context) {
        super(context);
        init();
    }

    public OfferCarouselWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfferCarouselWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        // Initialize image loader due it is going to be used outside WakupActivity
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext()).
                defaultDisplayImageOptions(ImageOptions.get()).
                build();
        ImageLoader.getInstance().init(config);

        inflate(getContext(), R.layout.wk_widget_offers_carousel, this);
        carouselView = (CarouselView) findViewById(R.id.carouselView);
    }

    public void loadOffers(Location location) {
        this.location = location;
        if (location != null) {
            RequestClient.getSharedInstance(getContext()).getFeaturedOffers(location, new OfferListRequestListener() {
                @Override
                public void onSuccess(List<Offer> offers) {
                    displayOffers(offers);
                }

                @Override
                public void onError(Exception exception) {
                    displayError("Could not load offers");
                }
            });
        } else {
            displayError("Location is not enabled");
        }
    }

    public void displayOffers(List<Offer> offers) {
        CarouselViewListener viewListener = new CarouselViewListener(offers);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(offers.size());
    }

    public void displayError(String errorMessage) {
        new AlertDialog.Builder(getContext()).setTitle(errorMessage).create();
    }

    class CarouselViewListener implements ViewListener {

        List<Offer> offers;

        public CarouselViewListener(List<Offer> offers) {
            this.offers = offers;
        }

        @Override
        public View setViewForPosition(final int position) {
            OfferCarouselItemView offerView = new OfferCarouselItemView(getContext());
            Offer offer = offers.get(position);
            offerView.setOffer(offer, location);
            int paddingBottom = getResources().getDimensionPixelSize(R.dimen.wk_coupon_padding);
            int padding = getResources().getDimensionPixelSize(R.dimen.wk_carousel_widget_padding);
            offerView.setPadding(padding, 0, padding, paddingBottom);
            return offerView;
        }
    }
}
