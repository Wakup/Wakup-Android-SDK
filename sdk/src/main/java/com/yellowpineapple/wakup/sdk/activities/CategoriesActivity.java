package com.yellowpineapple.wakup.sdk.activities;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.requests.offers.GetCategoriesRequest;
import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.models.Company;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends OfferListActivity {

    private List<Category> categories = null;
    private Category selectedCategory = null;
    private Company selectedCompany = null;
    private boolean alreadyRegistered = false;


    // Views
    View navigationView;
    PullToRefreshLayout ptrLayout;
    View emptyView;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.wk_activity_offers);

        injectViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(getPersistence().getOptions().showBackInRoot());
        }
    }

    protected void injectViews() {
        super.injectViews();
        emptyView = findViewById(R.id.emptyView);
        recyclerView = findViewById(R.id.recycler_view);
        navigationView = findViewById(R.id.navigationView);
        ptrLayout = findViewById(R.id.ptr_layout);
        // Set actions for Navigation bar
        View btnBigOffer = findViewById(R.id.btnBigOffer);
        if (btnBigOffer != null) {
            btnBigOffer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bigOfferPressed();
                }
            });
        }
        View btnMap = findViewById(R.id.btnMap);
        if (btnMap != null) {
            btnMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapButtonPressed();
                }
            });
        }
        View btnMyOffers = findViewById(R.id.btnMyOffers);
        if (btnMyOffers != null) {
            btnMyOffers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myOffersPressed();
                }
            });
        }
        afterViews();
    }

    void afterViews() {
        loadCategories(new GetCategoriesRequest.Listener() {
            @Override
            public void onSuccess(List<Category> offers) {
                CategoriesActivity.this.categories = categories;
                setupOffersGrid(recyclerView, navigationView, emptyView);
            }

            @Override
            public void onError(Exception exception) {
                setLoading(false);
                displayErrorDialog(getString(R.string.wk_connection_error_message));
                setEmptyViewVisible(true);
            }
        });
        setupOffersGrid(recyclerView, navigationView, emptyView);
    }

    void loadCategories(@Nullable final GetCategoriesRequest.Listener listener) {
        getRequestClient().getCategories(listener);
    }

    @Override
    void onRequestOffers(final int page, final Location location) {
        if (alreadyRegistered) {
            if (categories == null) {
                getRequestClient().getCategories(new GetCategoriesRequest.Listener() {
                    @Override
                    public void onSuccess(List<Category> categories) {
                        CategoriesActivity.this.categories = categories;
                        // TODO using default category for filtering
                        onRequestOffers(page, currentLocation);
                    }

                    @Override
                    public void onError(Exception exception) {
                        setLoading(false);
                        displayErrorDialog(getString(R.string.wk_connection_error_message));
                        setEmptyViewVisible(true);
                    }
                });
            } else {
                offersRequest = getRequestClient().findCategoryOffers(currentLocation, selectedCategory,
                        selectedCompany, page, getOfferListRequestListener());
            }
//            offersRequest = getRequestClient().findOffers(location, page, getOfferListRequestListener());
        } else {
            getWakup().register(new Wakup.RegisterListener() {
                @Override
                public void onSuccess() {
                    alreadyRegistered = true;
                    onRequestOffers(page, location);
                }

                @Override
                public void onError(Exception exception) {
                    getOfferListRequestListener().onError(exception);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    void bigOfferPressed() {
        String bigOfferUrl = getWakup().getBigOffer();
        WebViewActivity.intent(this).
                url(bigOfferUrl).
                title(getString(R.string.wk_activity_big_offer)).
                linksInBrowser(true).
                start();
        slideInTransition();
    }

    void mapButtonPressed() {
        int MAX_MAP_OFFERS = 20;
        List<Offer> mapOffers = new ArrayList<>(offers.subList(0, Math.min(MAX_MAP_OFFERS, offers.size())));
        OfferMapActivity.intent(this).offers(mapOffers).location(currentLocation).start();
        slideInTransition();
    }

    void myOffersPressed() {
        SavedOffersActivity.intent(this).start();
        slideInTransition();
    }

    @Override
    public PullToRefreshLayout getPullToRefreshLayout() {
        return ptrLayout;
    }

    void menuSearchSelected() {
        SearchActivity.intent(this).location(currentLocation).start();
        slideInTransition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.wk_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (handled) {
            return true;
        }
        int itemId_ = item.getItemId();
        if (itemId_ == R.id.menu_search) {
            menuSearchSelected();
            return true;
        }
        return false;
    }

    // Builder
    public static Builder intent(Context context) {
        return new Builder(context);
    }

    public static class Builder extends IntentBuilder<CategoriesActivity> {
        public Builder(Context context) {
            super(CategoriesActivity.class, context);
        }
    }
}