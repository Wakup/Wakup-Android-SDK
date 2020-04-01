package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.models.CompanyDetail;

import java.lang.reflect.Type;
import java.util.List;

public class HighlightedCompaniesRequest extends BaseRequest {

    public interface Listener extends Request.ErrorListener {
        void onSuccess(List<CompanyDetail> offers);
    }

    /* Constants */
	/* Segments */
    private final static String[] SEGMENTS = new String[] { "highlightedCompanies" };

    /* Properties */
    private Listener listener;

    public HighlightedCompaniesRequest(Listener listener) {
        super();
        this.httpMethod = HttpMethod.GET;
        addSegmentParams(SEGMENTS);
        this.listener = listener;
    }

    @Override
    protected void onSuccess(JsonElement response) {
        try {
            Type type = new TypeToken<List<CompanyDetail>>() {}.getType();
            List<CompanyDetail> companies = getParser().fromJson(response, type);
            listener.onSuccess(companies);
        } catch (JsonSyntaxException e) {
            getListener().onError(e);
        }
    }

    @Override
    public ErrorListener getListener() {
        return listener;
    }
}
