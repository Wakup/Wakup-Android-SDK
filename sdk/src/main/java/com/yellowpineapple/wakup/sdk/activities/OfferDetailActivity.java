package com.yellowpineapple.wakup.sdk.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.views.OfferDetailView;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

public class OfferDetailActivity extends OfferListActivity implements OfferDetailView.Listener {

    public final static String OFFER_EXTRA = "offer";
    public final static String LOCATION_EXTRA = "location";
    public final static String FROM_STORE_OFFERS_EXTRA = "fromStoreOffers";
    Offer offer;
    Location location;
    boolean fromStoreOffers = false;
    Toolbar toolbar;

    /* Views */
    RecyclerView recyclerView;
    PullToRefreshLayout ptrLayout;
    OfferDetailView offerDetailView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wk_activity_offers_list);

        setupToolbar();

        injectExtras();
        injectViews();

        if (offerDetailView == null) {
            offerDetailView = new OfferDetailView(this);
            offerDetailView.setListener(this);
            //recyclerView.addHeaderView(headerView);
            //recyclerView.addHeaderView(new RelatedOffersHeader(this));
        }
        setSubtitle(offer.getCompany().getName());
        offerDetailView.setOffer(offer, location);
        setupOffersGrid(offerDetailView, recyclerView, null, null);
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void injectExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            if (extras.containsKey(OFFER_EXTRA)) {
                offer = ((Offer) extras.getSerializable(OFFER_EXTRA));
            }
            if (extras.containsKey(LOCATION_EXTRA)) {
                location = extras.getParcelable(LOCATION_EXTRA);
            }
            if (extras.containsKey(FROM_STORE_OFFERS_EXTRA)) {
                fromStoreOffers = extras.getBoolean(FROM_STORE_OFFERS_EXTRA);
            }
        }
    }

    protected void injectViews() {
        super.injectViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.grid_view);
        ptrLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
    }

    @Override
    void onRequestOffers(final int page, final Location location) {
        offersRequest = getRequestClient().relatedOffers(offer, page, PER_PAGE, getOfferListRequestListener());
    }

    /* OfferDetailView.Listener */

    @Override
    public void onViewOnMapClicked(Offer offer) {
        displayInMap(offer, currentLocation);
    }

    @Override
    public void onDescriptionClicked(Offer offer) {
        ModalTextActivity.intent(this, offer.getDescription()).start();
    }

    @Override
    public void onSaveClicked(Offer offer) {
        PersistenceHandler persistence = getPersistence();
        if (persistence.isSavedOffer(offer)) {
            removeSavedOffer(offer);
        } else {
            saveOffer(offer);
        }
    }

    @Override
    public void onOpenLinkClicked(Offer offer) {
        openOfferLink(offer);
    }

    @Override
    public void onShareClicked(Offer offer) {
        shareOffer(offer);
    }

    @Override
    public PullToRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    @Override
    public void onStoreOffersClicked(Offer offer) {
        if (fromStoreOffers) {
            onBackPressed();
        } else {
            StoreOffersActivity.intent(this).offer(offer).location(location).start();
            slideInTransition();
        }
    }

    // Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wk_offer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (handled) {
            return true;
        }
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.menu_report) {
            reportOffer(this.offer);
            return true;
        }
        return false;
    }

    // Builder

    public static Builder intent(Context context) {
        return new Builder(context);
    }

    public static class Builder extends IntentBuilder<OfferDetailActivity> {

        public Builder(Context context) {
            super(OfferDetailActivity.class, context);
        }

        public Builder offer(Offer offer) {
            getIntent().putExtra(OFFER_EXTRA, offer);
            return this;
        }

        public Builder location(Location location) {
            getIntent().putExtra(LOCATION_EXTRA, location);
            return this;
        }

        public Builder fromStoreOffers(boolean fromStoreOffers) {
            getIntent().putExtra(FROM_STORE_OFFERS_EXTRA, fromStoreOffers);
            return this;
        }

    }
}
