package com.yellowpineapple.wakup.sdk.views;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.WakupOptions;
import com.yellowpineapple.wakup.sdk.models.Offer;

import me.grantland.widget.AutofitTextView;

/**
 * Created by agutierrez on 2/11/16.
 */
public abstract class OfferSmallView extends FrameLayout {

    Offer offer;

    /* Views */
    RemoteImageView offerImageView;
    TextView txtCompany;
    TextView txtShortDescription;
    TextView txtDescription;
    ViewGroup viewShortOffer;
    TextView txtDistance;
    TextView txtExpiration;
    View rippleView;

    WakupOptions options = null;

    Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onClick(Offer offer);
        void onLongClick(Offer offer);
    }

    public OfferSmallView(Context context) {
        super(context);
        init(null, 0);
    }

    public OfferSmallView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public OfferSmallView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    abstract int getLayoutResource();

    private void init(AttributeSet attrs, int defStyle) {
        injectViews();
    }

    private void injectViews() {
        inflate(getContext(), getLayoutResource(), this);
        txtDistance = findViewById(R.id.txtDistance);
        viewShortOffer = findViewById(R.id.viewShortOffer);
        txtExpiration = findViewById(R.id.txtExpiration);
        txtShortDescription = findViewById(R.id.txtShortDescription);
        txtDescription = findViewById(R.id.txtDescription);
        txtCompany = findViewById(R.id.txtCompany);
        offerImageView = findViewById(R.id.offerImageView);
        rippleView = findViewById(R.id.rippleView);

        rippleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) listener.onClick(offer);
            }
        });

        rippleView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (listener != null) listener.onLongClick(offer);
                return false;
            }
        });

    }

    public void setOffer(Offer offer, Location currentLocation) {
        this.offer = offer;
        offerImageView.setImage(offer.getThumbnail());
        if (txtCompany != null) txtCompany.setText(offer.getCompany().getName());
        if (txtShortDescription != null) txtShortDescription.setText(offer.getShortDescription());
        if (txtDescription != null) txtDescription.setText(offer.getDescription());
        if (txtDistance != null) txtDistance.setText(offer.getHumanizedDistance(getContext(), currentLocation));
        if (txtExpiration != null) txtExpiration.setText(offer.getHumanizedExpiration(getContext()));
        createShortOfferLabel(offer);
    }

    private void createShortOfferLabel(Offer offer) {
        if (viewShortOffer != null) {
            viewShortOffer.removeAllViews();

            float maxSize = getContext().getResources().getDimension(R.dimen.wk_title_text);
            float minSize = getContext().getResources().getDimension(R.dimen.wk_small_text);

            AutofitTextView txtShortOffer = (AutofitTextView) ((Activity) getContext()).getLayoutInflater().inflate(R.layout.wk_textview_shortoffer, null);
            txtShortOffer.setMaxTextSize(TypedValue.COMPLEX_UNIT_PX, maxSize);
            txtShortOffer.setMinTextSize(TypedValue.COMPLEX_UNIT_PX, minSize);
            txtShortOffer.setText(offer.getShortOffer());
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            viewShortOffer.addView(txtShortOffer, layoutParams);
        }
    }

    public Offer getOffer() {
        return offer;
    }

    public void setWakupOptions(WakupOptions options) {
        this.options = options;
        if (options != null) {
            if (txtCompany != null) {
                txtCompany.setVisibility(options.isCompaniesVisible() ? VISIBLE : GONE);
            }
            View viewDistance = findViewById(R.id.viewDistance);
            if (viewDistance != null) {
                viewDistance.setVisibility(options.isLocationEnabled() ? VISIBLE : GONE);
            }
            View viewExpiration = findViewById(R.id.viewExpiration);
            if (viewExpiration != null) {
                viewExpiration.setVisibility(options.isExpirationVisible() ? VISIBLE : GONE);
            }
        }
    }
}
