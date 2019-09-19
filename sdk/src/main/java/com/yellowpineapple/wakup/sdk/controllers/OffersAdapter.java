package com.yellowpineapple.wakup.sdk.controllers;

import android.content.Context;
import android.location.Location;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.views.OfferListView;

import java.util.List;

/***
 * ADAPTER
 */

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OfferListView.Listener {

    private List<Offer> offers;
    private Context context;
    private Location currentLocation;
    private Listener listener;
    private View headerView;
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
                return new HeaderViewHolder(headerView);
            default:
                OfferListView offerView = new OfferListView(getContext());
                offerView.setListener(this);
                return new OfferViewHolder(offerView);
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

    private Offer getOffer(int position) {
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

        return count;
    }

    private boolean isHeaderPresent() {
        return headerView != null;
    }

    private static class OfferViewHolder extends RecyclerView.ViewHolder {

        OfferListView offerView;
        OfferViewHolder(View v) {
            super(v);
            offerView = (OfferListView) v;
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        HeaderViewHolder(View v) {
            super(v);
        }
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

    public Context getContext() {
        return context;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}