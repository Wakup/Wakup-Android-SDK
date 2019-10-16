package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.utils.Strings;

/**
 * Created by agutierrez on 09/02/15.
 */
public class OfferDetailView
        extends LinearLayout
        implements OfferTagsView.OnTagSelectedListener, OfferCouponSummaryView.CouponSummaryListener {

    public interface Listener extends OfferTagsView.OnTagSelectedListener, OfferCouponSummaryView.CouponSummaryListener {
        void onViewOnMapClicked(Offer offer);
        void onSaveClicked(Offer offer);
        void onOpenLinkClicked(Offer offer);
        void onShareClicked(Offer offer);
        void onDescriptionClicked(Offer offer);
        void onStoreOffersClicked(Offer offer);
        void onTagSelected(String tag);
    }

    Offer offer;
    Listener listener;

    /* Views */
    RemoteImageView imgCompany;
    TextView txtCompany;
    TextView txtAddress;
    TextView txtDistance;
    RemoteImageView imgOffer;
    TextView txtShortDescription;
    TextView txtDescription;
    TextView txtShortOffer;
    TextView txtExpiration;
    View viewDescription;
    View storeView;
    ImageView imgDisclosureAddress;
    View storeOffersView;
    TextView txtStoreOffers;
    View tagsViewHolder;
    OfferTagsView tagsView;
    View couponViewHolder;
    OfferCouponSummaryView couponView;

    OfferActionButton btnWebsite;
    OfferActionButton btnMap;
    OfferActionButton btnSave;
    OfferActionButton btnShare;

    public OfferDetailView(Context context) {
        super(context);
        init();
    }

    public OfferDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfferDetailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        injectViews();

    }

    public void setOffer(Offer offer, Location location) {
        this.offer = offer;
        if (offer != null) {
            imgCompany.setImage(offer.getCompany().getLogo());
            imgOffer.setImage(offer.getImage(), offer.getThumbnail());
            txtCompany.setText(offer.getCompany().getName());
            txtDistance.setText(offer.getHumanizedDistance(getContext(), location));
            if (offer.getStore() != null) {
                txtAddress.setText(offer.getStore().getAddress());
                txtAddress.setVisibility(VISIBLE);
            } else {
                txtAddress.setVisibility(GONE);
            }
            viewDescription.setVisibility(Strings.isEmpty(offer.getDescription()) ? GONE : VISIBLE);
            txtDescription.setText(offer.getDescription());
            txtShortDescription.setText(offer.getShortDescription());
            txtShortOffer.setText(offer.getShortOffer());
            txtExpiration.setText(offer.getHumanizedExpiration(getContext()));

            // Offers count
            txtStoreOffers.setText(String.valueOf(offer.getCompany().getOfferCount()));
            storeOffersView.setVisibility(offer.getCompany().getOfferCount() > 1 ? VISIBLE : GONE);

            // Tags
            tagsViewHolder.setVisibility(offer.getTags().size() > 0 ? VISIBLE : GONE);
            tagsView.setOffer(offer);

            // Coupons
            couponViewHolder.setVisibility(offer.getRedemptionCode() != null ? VISIBLE : GONE);
            couponView.setOffer(offer);

            boolean hasLocation = offer.hasLocation();
            imgDisclosureAddress.setVisibility(hasLocation ? VISIBLE : GONE);
            storeView.setClickable(hasLocation);
            btnMap.setEnabled(hasLocation);
            btnWebsite.setEnabled(offer.hasLink());
            refreshSavedState();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    void refreshSavedState() {
        boolean saved = PersistenceHandler.getSharedInstance(getContext()).isSavedOffer(offer);
        btnSave.setText(saved ? R.string.wk_action_offer_saved : R.string.wk_action_offer_save);
        btnSave.setSelected(saved);
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_view_offer_detail, this);
        btnShare = ((OfferActionButton) findViewById(R.id.btnShare));
        imgOffer = ((RemoteImageView) findViewById(R.id.imgOffer));
        txtExpiration = ((TextView) findViewById(R.id.txtExpiration));
        txtDescription = ((TextView) findViewById(R.id.txtDescription));
        imgCompany = ((RemoteImageView) findViewById(R.id.imgCompany));
        btnWebsite = ((OfferActionButton) findViewById(R.id.btnWebsite));
        txtShortOffer = ((TextView) findViewById(R.id.txtShortOffer));
        txtStoreOffers = ((TextView) findViewById(R.id.txtStoreOffers));
        txtShortDescription = ((TextView) findViewById(R.id.txtShortDescription));
        imgDisclosureAddress = ((ImageView) findViewById(R.id.imgDisclosureAddress));
        storeOffersView = findViewById(R.id.storeOffersView);
        viewDescription = findViewById(R.id.viewDescription);
        btnMap = ((OfferActionButton) findViewById(R.id.btnMap));
        storeView = findViewById(R.id.storeView);
        txtCompany = ((TextView) findViewById(R.id.txtCompany));
        txtAddress = ((TextView) findViewById(R.id.txtAddress));
        txtDistance = ((TextView) findViewById(R.id.txtDistance));
        btnSave = ((OfferActionButton) findViewById(R.id.btnSave));
        tagsViewHolder = findViewById(R.id.tagsViewHolder);
        tagsView = (OfferTagsView) findViewById(R.id.tagsView);
        tagsView.setOnTagSelectedListener(this);
        couponViewHolder = findViewById(R.id.couponViewHolder);
        couponView = (OfferCouponSummaryView) findViewById(R.id.couponView);
        couponView.setListener(this);

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == btnSave) {
                    saveOffer();
                } else if (view == btnWebsite) {
                    openWebsite();
                } else if (view == viewDescription) {
                    onDescriptionClicked();
                } else if (view == btnShare) {
                    shareOffer();
                } else if (view == storeOffersView) {
                    openStoreDetails();
                } else if (view == btnMap) {
                    openMap();
                } else if (view == storeView) {
                    onAddressClicked();
                }
            }
        };

        View[] onClickViews = new View[] {
                btnSave, btnWebsite, viewDescription, btnShare, storeOffersView, btnMap, storeView
        };
        for (View view : onClickViews) {
            view.setOnClickListener(clickListener);
        }
    }

    void onDescriptionClicked() {
        if (listener != null) listener.onDescriptionClicked(offer);
    }

    void onAddressClicked() {
        if (listener != null) listener.onViewOnMapClicked(offer);
    }

    void openMap() { if (listener != null) listener.onViewOnMapClicked(offer); }

    void openWebsite() { if (listener != null) listener.onOpenLinkClicked(offer); }

    void saveOffer() {
        if (listener != null) listener.onSaveClicked(offer);
        refreshSavedState();
    }

    void shareOffer() { if (listener != null) listener.onShareClicked(offer); }

    void openStoreDetails() { if (listener != null) listener.onStoreOffersClicked(offer); }

    @Override
    public void onTagSelected(String tag) {
        if (listener != null) listener.onTagSelected(tag);
    }

    @Override
    public void onCouponSelected(Offer offer) {
        if (listener != null) listener.onCouponSelected(offer);
    }
}
