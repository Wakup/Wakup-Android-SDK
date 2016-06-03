package com.yellowpineapple.wakup.sdk.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

public class StoreOffersActivity extends OfferListActivity {

    public final static String LOCATION_EXTRA = "location";
    public final static String OFFER_EXTRA = "offer";
    Offer offer;
    Location location;

    /* Views */
    RecyclerView gridView;
    PullToRefreshLayout ptrLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wk_activity_offers_list);
        injectExtras();
        injectViews();
    }

    protected void injectViews() {
        super.injectViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        gridView = ((RecyclerView) findViewById(R.id.grid_view));
        ptrLayout = ((PullToRefreshLayout) findViewById(R.id.ptr_layout));
        afterViews();
    }

    private void injectExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            if (extras.containsKey(LOCATION_EXTRA)) {
                location = extras.getParcelable(LOCATION_EXTRA);
            }
            if (extras.containsKey(OFFER_EXTRA)) {
                offer = ((Offer) extras.getSerializable(OFFER_EXTRA));
            }
        }
    }

    void afterViews() {
        setTitle(offer.getCompany().getName());
        setupOffersGrid(gridView);
    }

    @Override
    void onRequestOffers(final int page, final Location location) {
        offersRequest = getRequestClient().getCompanyOffers(offer.getCompany(), offer.getStore(), page, getOfferListRequestListener());
    }

    @Override
    public PullToRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    protected void showOfferDetail(Offer offer, Location currentLocation) {
        OfferDetailActivity.intent(this).offer(offer).location(currentLocation).fromStoreOffers(true).start();
        slideInTransition();
    }

    // Builder

    public static Builder intent(Context context) {
        return new Builder(context);
    }

    public static class Builder extends IntentBuilder<StoreOffersActivity> {

        public Builder(Context context) {
            super(StoreOffersActivity.class, context);
        }

        public Builder location(Location location) {
            getIntent().putExtra(LOCATION_EXTRA, location);
            return this;
        }

        public Builder offer(Offer offer) {
            getIntent().putExtra(OFFER_EXTRA, offer);
            return this;
        }
    }
}
