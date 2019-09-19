package com.yellowpineapple.wakup.sdk.communications.requests;

import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.models.Offer;

public interface OfferRequestListener extends Request.ErrorListener {
    void onSuccess(Offer offer);
}
