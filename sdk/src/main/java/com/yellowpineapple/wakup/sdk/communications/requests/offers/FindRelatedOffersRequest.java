package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import android.location.Location;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.models.Company;
import com.yellowpineapple.wakup.sdk.models.Offer;

import java.lang.reflect.Type;
import java.util.List;

    public class FindRelatedOffersRequest extends BaseRequest {

        /* Constants */
        /* Segments */
        private final static String[] SEGMENTS = new String[] { "offers", "category", "related" };

        /* Properties */
        private OfferListRequestListener listener;

        public FindRelatedOffersRequest(Location location, Category category, Company company, int page, int perPage, OfferListRequestListener listener) {
            super();
            this.httpMethod = HttpMethod.GET;
            addPagination(page, perPage);
            addSegmentParams(SEGMENTS);
            if (location != null) {
                addParam("latitude", location.getLatitude());
                addParam("longitude", location.getLongitude());
            }
            if (company != null)    addParam("companyId", company.getId());
            if (category != null)   addParam("categoryId", category.getId());
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

