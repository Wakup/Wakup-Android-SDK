package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.location.Location;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;

/**
 * Created by agutierrez on 09/02/15.
 */
public class OfferMapInfoView extends LinearLayout {

    Offer offer;
    /* Views */
    TextView txtCompany;
    TextView txtAddress;
    TextView txtDistance;
    View imgDisclosure;

    public OfferMapInfoView(Context context) {
        super(context);
        init();
    }

    public OfferMapInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfferMapInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        injectViews();
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_view_map_offer_info, this);
        txtDistance = findViewById(R.id.txtDistance);
        txtCompany = findViewById(R.id.txtCompany);
        txtAddress = findViewById(R.id.txtAddress);
        imgDisclosure = findViewById(R.id.imgDisclosure);
    }

    public void setOffer(Offer offer, Location location) {
        this.offer = offer;
        if (offer != null) {
            txtCompany.setText(offer.getCompany().getName());
            if (offer.getStore() != null) {
                txtAddress.setText(offer.getStore().getAddress());
                txtAddress.setVisibility(VISIBLE);
            } else {
                txtAddress.setVisibility(GONE);
            }
            txtDistance.setText(offer.getHumanizedDistance(getContext(), location));
            imgDisclosure.setVisibility(isClickable() ? VISIBLE : GONE);
        }
    }
}
