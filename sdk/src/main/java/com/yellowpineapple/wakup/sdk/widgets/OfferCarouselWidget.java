package com.yellowpineapple.wakup.sdk.widgets;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;

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
public class OfferCarouselWidget extends Widget implements OfferSmallView.Listener {

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
        afterViews();
    }

    public void loadOffers(Location location) {
        int DEFAULT_OFFER_COUNT = 5;
        loadOffers(location, DEFAULT_OFFER_COUNT);
    }

    public void loadOffers(Location location, int offerCount) {
        this.location = location;
        if (location != null) {
            loadingView.setVisible(true);
            loadingView.setLoading(true);
            int FIRST_PAGE = 0;
            RequestClient.getSharedInstance(getContext()).getFeaturedOffers(location, FIRST_PAGE, offerCount, new OfferListRequestListener() {
                @Override
                public void onSuccess(List<Offer> offers) {
                    displayOffers(offers);
                    loadingView.setLoading(false);
                    loadingView.setVisible(false);
                }

                @Override
                public void onError(Exception exception) {
                    displayError(getContext().getString(R.string.wk_connection_error_message));
                }
            });
        } else {
            displayLocationError();
        }
    }

    public void displayOffers(List<Offer> offers) {
        CarouselViewListener viewListener = new CarouselViewListener(offers);
        carouselView.setViewListener(viewListener);
        carouselView.setPageCount(offers.size());
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
            offerView.setListener(OfferCarouselWidget.this);
            int paddingBottom = getResources().getDimensionPixelSize(R.dimen.wk_coupon_padding);
            int padding = getResources().getDimensionPixelSize(R.dimen.wk_carousel_widget_padding);
            offerView.setPadding(padding, 0, padding, paddingBottom);
            return offerView;
        }
    }

    @Override
    public void onClick(Offer offer) {
        Wakup.instance(getContext()).launchWithOffer(offer);
    }

    @Override
    public void onLongClick(Offer offer) {
    }
}
