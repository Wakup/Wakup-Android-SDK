package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;

import com.yellowpineapple.wakup.sdk.R;

/**
 * Created by agutierrez on 3/11/16.
 */
public class OfferCarouselItemView extends OfferSmallView {

    public OfferCarouselItemView(Context context) {
        super(context);
    }

    @Override
    int getLayoutResource() {
        return R.layout.wk_view_carousel_offer;
    }
}
