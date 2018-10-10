package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RedemptionCodeDetail;

import java.lang.reflect.Type;

public class GetRedemptionCodeRequest extends BaseRequest {

    public interface Listener extends ErrorListener {
        void onSuccess(RedemptionCodeDetail codeDetail);
    }

    /* Properties */
    private Listener listener;

    public GetRedemptionCodeRequest(Offer offer, Listener listener) {
        super();
        this.httpMethod = HttpMethod.GET;
        final String[] segments = new String[] { "offers", Integer.toString(offer.getId()), "code" };
        addSegmentParams(segments);
        this.listener = listener;
    }

    @Override
    protected void onSuccess(JsonElement response) {
        try {
            Type type = new TypeToken<RedemptionCodeDetail>() {}.getType();
            RedemptionCodeDetail codeDetail = getParser().fromJson(response, type);
            listener.onSuccess(codeDetail);
        } catch (JsonSyntaxException e) {
            getListener().onError(e);
        }
    }

    @Override
    public ErrorListener getListener() {
        return listener;
    }
}
