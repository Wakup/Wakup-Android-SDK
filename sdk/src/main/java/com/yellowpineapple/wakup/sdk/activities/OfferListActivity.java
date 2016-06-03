package com.yellowpineapple.wakup.sdk.activities;

import android.graphics.Rect;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.yellowpineapple.wakup.sdk.controllers.OffersAdapter;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.views.PullToRefreshLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agutierrez on 09/02/15.
 */
public abstract class OfferListActivity extends ParentActivity implements OffersAdapter.Listener {

    OffersAdapter offersAdapter;
    boolean mHasRequestedMore;
    boolean mHasMoreResults = false;
    Request offersRequest = null;
    int offersPage = FIRST_PAGE;

    Location currentLocation = null;

    boolean offersLoaded = false;
    List<Offer> offers = new ArrayList<>();
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

    protected boolean shouldReloadOffers() {
        return !offersLoaded;
    }

    void setupOffersGrid(RecyclerView recyclerView) {
        setupOffersGrid(recyclerView, null, null);
    }

    void setupOffersGrid(RecyclerView recyclerView, View navigationView, View emptyView) {
        setupOffersGrid(null, recyclerView, navigationView, emptyView);
    }

    void setupOffersGrid(View headerView, RecyclerView recyclerView, View navigationView, View emptyView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.navigationView = navigationView;

        registerForContextMenu(recyclerView);

        if (getPullToRefreshLayout() != null) {
            setupPullToRefresh(getPullToRefreshLayout());
        }

        if (emptyView != null) emptyView.setVisibility(View.GONE);

        offersAdapter = new OffersAdapter(headerView, this);
        offersAdapter.setListener(this);

        // do we have saved data?
        if (shouldReloadOffers()) reloadOffers();

        offersAdapter.setOffers(offers);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        recyclerView.addItemDecoration(new SpaceItemDecoration(headerView != null ,
                getResources().getDimensionPixelSize(R.dimen.wk_card_gap)));
        recyclerView.setAdapter(offersAdapter);

        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (mHasMoreResults) {
                    onLoadMoreItems();
                }
            }
        });
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private int space;
        boolean hasHeader;

        public SpaceItemDecoration(boolean hasHeader, int space) {
            this.space = space;
            this.hasHeader = hasHeader;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);

            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            int spanIndex = lp.getSpanIndex();

            if(hasHeader) {
                if(position == 0) {
                    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(true);
                    outRect.top = space * 2;
                    outRect.left = space * 2;
                    outRect.right = space * 2;
                } else {
                    ((StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams()).setFullSpan(false);
                    if (spanIndex == 1) {
                        outRect.left = space;
                        outRect.right = space * 2;
                    } else {
                        outRect.left = space * 2;
                        outRect.right = space;
                    }
                }
                outRect.bottom = space * 2;
            } else {
                if(position == 0 || position == 1) {
                    outRect.top = space * 2;
                }
                if (position >= 0) {
                    if (spanIndex == 1) {
                        outRect.left = space;
                        outRect.right = space * 2;
                    } else {
                        outRect.left = space * 2;
                        outRect.right = space;
                    }

                    outRect.bottom = space * 2;
                }
            }
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
                onRequestOffers(page, location);
            }

            @Override
            public void onLocationError(Exception exception) {
                currentLocation = getWakup().getOptions().getDefaultLocation();
                onRequestOffers(page, currentLocation);
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
        mHasMoreResults = newOffers.size() >= PER_PAGE;
        if (page == FIRST_PAGE) {
            this.offers = newOffers;
        } else {
            this.offers.addAll(newOffers);
        }

        getOffersAdapter().setCurrentLocation(currentLocation);
        getOffersAdapter().setOffers(this.offers);
        getOffersAdapter().notifyDataSetChanged();
        mHasRequestedMore = false;
        setLoading(false);
        offersRequest = null;
        // Wearable offers are disabled for production
        if (page == FIRST_PAGE) {
            //showWearableOffers(offers);
        }
        setEmptyViewVisible(offers.size() == 0);
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
    public void onOfferClick(Offer offer) {
        showOfferDetail(offer, currentLocation);
    }

    @Override
    public void onOfferLongClick(Offer offer) {
        this.selectedOffer = offer;
        //openContextMenu(recyclerView);
    }

    abstract void onRequestOffers(final int page, final Location currentLocation);

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

    public OffersAdapter getOffersAdapter() {
        return offersAdapter;
    }

    public List<Offer> getOffers() {
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
