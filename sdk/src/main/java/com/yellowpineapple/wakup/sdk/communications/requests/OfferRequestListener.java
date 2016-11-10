package com.yellowpineapple.wakup.sdk.communications.requests;

import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.models.Offer;

import java.util.List;

public interface OfferRequestListener extends Request.ErrorListener {
    void onSuccess(Offer offer);
}
