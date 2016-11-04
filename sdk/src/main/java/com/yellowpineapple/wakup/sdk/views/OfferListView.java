package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;

import com.yellowpineapple.wakup.sdk.R;

public class OfferListView extends OfferSmallView {

    public OfferListView(Context context) {
        super(context);
    }

    @Override
    int getLayoutResource() {
        return R.layout.wk_list_item_offer;
    }
}
