package com.yellowpineapple.wakup.sdk.controllers;

import android.content.Context;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.views.OfferDetailView;
import com.yellowpineapple.wakup.sdk.views.OfferListView;

import java.util.List;

/***
 * ADAPTER
 */

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OfferListView.Listener {

    List<Offer> offers;
    boolean loading;
    Context context;
    Location currentLocation;
    Listener listener;
    View headerView;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public OffersAdapter(View headerView, final Context context) {
        super();
        this.headerView = headerView;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(headerView);
                return headerViewHolder;
            default:
                OfferListView offerView = new OfferListView(getContext());
                offerView.setListener(this);
                OfferViewHolder viewHolder = new OfferViewHolder(offerView);
                return viewHolder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return isHeaderPresent() && position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(getItemViewType(position) != TYPE_HEADER) {
            OfferViewHolder offerViewHolder = (OfferViewHolder) holder;
            offerViewHolder.offerView.setOffer(getOffer(position), currentLocation);
        }

    }

    Offer getOffer(int position) {
        return offers.get(isHeaderPresent() ? position - 1 : position);
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (offers != null) {
            count = offers.size();
        }
        if (isHeaderPresent()) {
            count++;
        }

        if (loading) {
            count++;
        }
        return count;
    }

    boolean isHeaderPresent() {
        return headerView != null;
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {

        public OfferListView offerView;
        public OfferViewHolder(View v) {
            super(v);
            offerView = (OfferListView) v;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public HeaderViewHolder(View v) {
            super(v);
        }
    }

    boolean isLoadingView(int position) {
        int size = offers != null ? offers.size() : 0;
        return position == size;
    }

    @Override
    public void onClick(Offer offer) {
        if (listener != null) listener.onOfferClick(offer);
    }

    @Override
    public void onLongClick(Offer offer) {
        if (listener != null) listener.onOfferLongClick(offer);
    }

    public interface Listener {
        void onOfferClick(Offer offer);
        void onOfferLongClick(Offer offer);
    }

    public Listener getListener() {
        return listener;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public List<Offer> getOffers() {
        return offers;
    }

    public void setOffers(List<Offer> offers) {
        this.offers = offers;
    }

    public boolean isLoading() {
        return loading;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public Context getContext() {
        return context;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}