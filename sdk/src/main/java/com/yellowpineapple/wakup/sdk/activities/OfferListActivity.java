package com.yellowpineapple.wakup.sdk.activities;

import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.controllers.MultipleOffersAdapter;
import com.yellowpineapple.wakup.sdk.controllers.OfferCategory;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class OfferListActivity extends ParentActivity implements MultipleOffersAdapter.Listener {

    MultipleOffersAdapter offersAdapter;
    OfferCategory currentCategory;
    boolean mHasRequestedMore;
    boolean mHasMoreResults = false;
    Request offersRequest = null;
    int offersPage = FIRST_PAGE;
    RecyclerView.ItemDecoration itemDecoration = null;

    Location currentLocation = null;

    boolean offersLoaded = false;
    boolean shouldReloadDataset = true;
    LinkedHashMap<OfferCategory, List<Offer>> offers = new LinkedHashMap<>();
    List<OfferCategory> offerCategories;
    Offer selectedOffer = null;

    static int FIRST_PAGE = BaseRequest.FIRST_PAGE;
    static int PER_PAGE = BaseRequest.RESULTS_PER_PAGE;

    private RecyclerView recyclerView;
    View navigationView;
    View emptyView;
    StaggeredGridLayoutManager staggeredGridLayoutManager;

    interface AnimationListener {
        void onAnimationCompleted();
    }

    final static int DEFAULT_CATEGORY = 1000;

    protected boolean shouldReloadOffers() {
        return !offersLoaded;
    }

    void setupOffersGrid(RecyclerView recyclerView) {
        setupOffersGrid(recyclerView, null, null);
    }

    void setupOffersGrid(RecyclerView recyclerView, View navigationView, View emptyView) {
        setupOffersGrid(null, recyclerView,
                Collections.singletonList(new OfferCategory(DEFAULT_CATEGORY, null)),
                navigationView, emptyView);
    }

    void setupOffersGrid(final View headerView, RecyclerView recyclerView,
                         List<OfferCategory> offerCategories,
                         View navigationView, View emptyView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.navigationView = navigationView;

        registerForContextMenu(recyclerView);

        if (getPullToRefreshLayout() != null) {
            setupPullToRefresh(getPullToRefreshLayout());
        }

        //TODO Init offer offerCategories
        offers.clear();
        for (OfferCategory category : offerCategories) {
            offers.put(category, new ArrayList<Offer>());
        }
        this.offerCategories = offerCategories;
        this.currentCategory = offerCategories.get(0);

        if (emptyView != null) emptyView.setVisibility(View.GONE);

        offersAdapter = new MultipleOffersAdapter(headerView, this);
        offersAdapter.setListener(this);


        // do we have saved data?
        if (shouldReloadOffers()) reloadOffers();

        offersAdapter.setOfferCategories(offers);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Set spacing
        if (itemDecoration != null) recyclerView.removeItemDecoration(itemDecoration);
        itemDecoration = new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.wk_card_gap));
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(offersAdapter);
        recyclerView.setItemAnimator(null);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (mHasMoreResults) {
                    onLoadMoreItems();
                }
            }
        });
        offersAdapter.notifyDataSetChanged();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int halfSpace;

        SpacesItemDecoration(int space) {
            this.halfSpace = space;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {

            if (parent.getPaddingLeft() != halfSpace) {
                parent.setPadding(halfSpace, halfSpace, halfSpace, halfSpace);
                parent.setClipToPadding(false);
            }

            outRect.top = halfSpace;
            outRect.bottom = halfSpace;
            outRect.left = halfSpace;
            outRect.right = halfSpace;
        }
    }

    public PullToRefreshLayout getPullToRefreshLayout() {
        return null;
    }

    void setupPullToRefresh(PullToRefreshLayout pullToRefreshLayout) {
        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadOffers();
            }
        });
        pullToRefreshLayout.setColorSchemeResources(R.color.wk_secondary, R.color.wk_primary);
        pullToRefreshLayout.setSwipeableChildren(recyclerView, emptyView);
    }

    protected void reloadOffers() {
        shouldReloadDataset = true;
        currentCategory = offerCategories.get(0);
        recyclerView.scrollToPosition(0);
        requestLoadPage(FIRST_PAGE);
    }

    protected void requestLoadPage(final int page) {
        this.offersPage = page;
        this.mHasRequestedMore = true;
        setLoading(true);
        if (offersRequest != null) {
            offersRequest.cancel();
        }
        getLastKnownLocation(new LocationListener() {
            @Override
            public void onLocationSuccess(final Location location) {
                currentLocation = location;
                onRequestOffers(currentCategory, page, location);
            }

            @Override
            public void onLocationError(Exception exception) {
                currentLocation = getWakup().getOptions().getDefaultLocation();
                onRequestOffers(currentCategory, page, currentLocation);
                if (!getPersistence().isLocationAsked()) {
                    Toast.makeText(OfferListActivity.this, R.string.wk_disabled_location, Toast.LENGTH_LONG).show();
                    getPersistence().setLocationAsked(true);
                }
            }
        });
    }

    @Override
    public void setLoading(final boolean loading) {
        if (getPullToRefreshLayout() != null) {
            getPullToRefreshLayout().post(new Runnable() {
                @Override
                public void run() {
                    getPullToRefreshLayout().setRefreshing(loading);
                }
            });
        } else {
            super.setLoading(loading);
        }
    }

    protected OfferListRequestListener getOfferListRequestListener() {
        return new OfferListRequestListener() {
            @Override
            public void onSuccess(List<Offer> offers) {
                setOffers(offersPage, offers);
                offersLoaded = true;
            }

            @Override
            public void onError(Exception exception) {
                mHasRequestedMore = false;
                setLoading(false);
                displayErrorDialog(getString(R.string.wk_connection_error_message));
                setEmptyViewVisible(OfferListActivity.this.offers.isEmpty());
                offersRequest = null;
            }
        };
    }

    void setOffers(int page, List<Offer> newOffers) {
        List<Offer> categoryOffers = offers.get(currentCategory);
        int previousSize = getOffersAdapter().getItemCount();
        if (shouldReloadDataset) {
            for (List<Offer> list : offers.values()) {
                list.clear();
            }
        }
        categoryOffers.addAll(newOffers);

        boolean loadNextCategoryNow = false;

        // Check if it should load more results
        boolean endOfCategory = newOffers.size() < PER_PAGE;
        mHasMoreResults = !endOfCategory;
        if (endOfCategory) {
            // End of page for current category
            List<OfferCategory> categoryList = new ArrayList<>(offers.keySet());
            int categoryIndex = categoryList.indexOf(currentCategory);
            if (categoryIndex + 1 < categoryList.size()) {
                // There is more offerCategories to fetch
                currentCategory = categoryList.get(categoryIndex + 1);
                mHasMoreResults = true;
                loadNextCategoryNow = true;
            }
        }

        getOffersAdapter().setCurrentLocation(currentLocation);
        getOffersAdapter().setOfferCategories(this.offers);
        if (shouldReloadDataset) {
            // Reload full dataset when loading first page of first category
            getOffersAdapter().notifyDataSetChanged();
            shouldReloadDataset = false;
        } else {
            // Animate only inserted items
            int newSize = offersAdapter.getItemCount();
            int insertedItems = newSize - previousSize;
            getOffersAdapter().notifyItemRangeInserted(previousSize, insertedItems);
        }
        mHasRequestedMore = false;
        setLoading(false);
        offersRequest = null;
        // Wearable offers are disabled for production
        //if (page == FIRST_PAGE) {
        //showWearableOffers(offers);
        //}
        setEmptyViewVisible(offers.size() == 0);
        if (loadNextCategoryNow) requestLoadPage(FIRST_PAGE);
    }

    void showWearableOffers(List<Offer> offers) {
        // TODO Should be done in background
        //NotificationFactory.getInstance(OfferListActivity.this).showWearableOffers(offers, currentLocation);
    }

    /* Empty view */

    void setEmptyViewVisible(boolean visible) {
        if (emptyView != null) {
            emptyView.setVisibility(visible ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(visible ? View.GONE : View.VISIBLE);
        }
    }

    void delayNavigationToggle(final boolean visible, final boolean animated, final AnimationListener listener) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleNavigationBarVisibility(visible, animated, listener);
            }
        }, 50);
    }

    private void toggleNavigationBarVisibility(final boolean visible, boolean animated, final AnimationListener listener) {
        if (navigationView != null) {
            if (animated) {
                Animation a = AnimationUtils.loadAnimation(this, visible ? R.anim.wk_slide_in_down : R.anim.wk_slide_out_up);
                a.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        navigationView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (!visible) navigationView.setVisibility(View.GONE);
                        if (listener != null) listener.onAnimationCompleted();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                navigationView.startAnimation(a);
            } else {
                navigationView.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        }
        if (listener != null) listener.onAnimationCompleted();
    }

    private void onLoadMoreItems() {
        // notify the adapter that we can update now
        mHasRequestedMore = false;
        requestLoadPage(offersPage + 1);
    }

    @Override
    protected void onResume() {
        //getSupportActionBar().show();
        toggleNavigationBarVisibility(true, false, null);
        super.onResume();
    }

    @Override
    public void onOfferClick(@NonNull Offer offer) {
        showOfferDetail(offer, currentLocation);
    }

    @Override
    public void onOfferLongClick(@NonNull Offer offer) {
        this.selectedOffer = offer;
        //openContextMenu(recyclerView);
    }

    abstract void onRequestOffers(OfferCategory category, final int page, final Location currentLocation);

    // Context menu

    enum OfferMenuItem {
        VIEW_IN_MAP(1, R.string.wk_menu_map),
        MY_OFFERS_SAVE(2, R.string.wk_menu_my_offers_add),
        MY_OFFERS_REMOVE(3, R.string.wk_menu_my_offers_remove),
        SHARE(4, R.string.wk_menu_share),
        REPORT(5, R.string.wk_menu_report);

        int id;
        int textResId;

        private OfferMenuItem(int id, int textResId) {
            this.id = id;
            this.textResId = textResId;
        }

        public static OfferMenuItem fromId(int id) {
            for (OfferMenuItem item : values()) {
                if (item.getId() == id) {
                    return item;
                }
            }
            return null;
        }

        public int getId() {
            return id;
        }

        public int getTextResId() {
            return textResId;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v == recyclerView) {
            if (selectedOffer.hasLocation()) {
                addMenuItem(menu, OfferMenuItem.VIEW_IN_MAP);
            }
            if (isSavedOffer(selectedOffer)) {
                addMenuItem(menu, OfferMenuItem.MY_OFFERS_REMOVE);
            } else {
                addMenuItem(menu, OfferMenuItem.MY_OFFERS_SAVE);
            }
            addMenuItem(menu, OfferMenuItem.SHARE);
            addMenuItem(menu, OfferMenuItem.REPORT);
        }
    }

    void addMenuItem(ContextMenu menu, OfferMenuItem menuItem) {
        menu.add(Menu.NONE, menuItem.getId(), Menu.NONE, menuItem.getTextResId());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        OfferMenuItem menuItem = OfferMenuItem.fromId(item.getItemId());
        switch (menuItem) {
            case VIEW_IN_MAP: displayInMap(selectedOffer, currentLocation); break;
            case MY_OFFERS_SAVE: saveOffer(selectedOffer); break;
            case MY_OFFERS_REMOVE: removeSavedOffer(selectedOffer); break;
            case SHARE: shareOffer(selectedOffer); break;
            case REPORT: reportOffer(selectedOffer); break;
        }
        afterContextItemSelected(menuItem);
        return true;
    }

    protected void afterContextItemSelected(OfferMenuItem menuItem) {

    }

    public MultipleOffersAdapter getOffersAdapter() {
        return offersAdapter;
    }

    public LinkedHashMap<OfferCategory, List<Offer>> getOffers() {
        return offers;
    }

    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
            visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                }
                else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            if (mLayoutManager instanceof StaggeredGridLayoutManager) {
                int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) mLayoutManager).findLastVisibleItemPositions(null);
                // get maximum element within the list
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions);
            } else if (mLayoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            } else if (mLayoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) mLayoutManager).findLastVisibleItemPosition();
            }

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount);
                loading = true;
            }
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount);

    }
}
