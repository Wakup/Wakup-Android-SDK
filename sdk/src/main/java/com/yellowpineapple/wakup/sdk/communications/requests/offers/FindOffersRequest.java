package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import android.location.Location;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Company;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.Strings;

import java.lang.reflect.Type;
import java.util.List;

public class FindOffersRequest extends BaseRequest {

    /* Constants */
	/* Segments */
    final static String[] SEGMENTS = new String[] { "offers", "find" };

    /* Properties */
    OfferListRequestListener listener;

    public FindOffersRequest(Location location, Double radiusInKm, OfferListRequestListener listener) {
        this(location, null, null, false, FIRST_PAGE, LOCATED_RESULTS_PER_PAGE, radiusInKm, listener);
    }

    public FindOffersRequest(Location location, Company company, List<String> tags, int page, OfferListRequestListener listener) {
        this(location, company, tags, true, page, RESULTS_PER_PAGE, null, listener);
    }

    public FindOffersRequest(Location location, Company company, List<String> tags, boolean includeOnline, int page, int perPage, Double radiusInKm, OfferListRequestListener listener) {
        super();
        this.httpMethod = HttpMethod.GET;
        addSegmentParams(SEGMENTS);
        addPagination(page, perPage);
        addParam("includeOnline", includeOnline);
        addParam("latitude", location.getLatitude());
        addParam("longitude", location.getLongitude());
        if (radiusInKm != null) addParam("radiusInKm", radiusInKm);
        if (company != null)    addParam("companyId", company.getId());
        if (tags != null && tags.size() > 0) {
            addParam("tags", Strings.join(",", tags));
        }
        this.listener = listener;
    }

    @Override
    protected void onSuccess(JsonElement response) {
        try {
            Type type = new TypeToken<List<Offer>>() {}.getType();
            List<Offer> offers = getParser().fromJson(response, type);
            listener.onSuccess(offers);
        } catch (JsonSyntaxException e) {
            getListener().onError(e);
        }
    }

    @Override
    public ErrorListener getListener() {
        return listener;
    }
}
