package com.yellowpineapple.wakup.sdk.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetRedemptionCodeRequest;
import com.yellowpineapple.wakup.sdk.controllers.OfferCategory;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RedemptionCodeDetail;
import com.yellowpineapple.wakup.sdk.models.SearchResultItem;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.views.OfferDetailView;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

import java.util.Collections;
import java.util.List;

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
        }
        if (isCompaniesVisible()) {
            setSubtitle(offer.getCompany().getName());
        }
        offerDetailView.setOptions(getOptions());
        offerDetailView.setOffer(offer, location);
        setupOffersGrid(offerDetailView, recyclerView,
                Collections.singletonList(new OfferCategory(DEFAULT_CATEGORY, getString(R.string.wk_related_offers))),
                null, null);
    }

    void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.grid_view);
        ptrLayout = findViewById(R.id.ptr_layout);
    }

    @Override
    void onRequestOffers(OfferCategory category, final int page, final Location location) {
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

    @Override
    public void onTagSelected(String tag) {
        SearchResultItem tagItem = SearchResultItem.tag(false, tag);
        List<String> tags = Collections.singletonList(tag);
        SearchResultActivity.intent(this).searchItem(tagItem).tags(tags).start();
        slideInTransition();
    }

    @Override
    public void onCouponSelected(final Offer offer) {
        setLoading(true);
        getRequestClient().getRedemptionCode(offer, new GetRedemptionCodeRequest.Listener() {
            @Override
            public void onSuccess(RedemptionCodeDetail codeDetail) {
                setLoading(false);
                OfferCouponsActivity.intent(OfferDetailActivity.this, offer, codeDetail).start();
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                displayErrorDialog(exception);
            }
        });
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
