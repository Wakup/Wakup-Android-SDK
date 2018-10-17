package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.models.Category;

import java.lang.reflect.Type;
import java.util.List;

public class GetCategoriesRequest extends BaseRequest {

    public interface Listener extends Request.ErrorListener {
        void onSuccess(List<Category> categories);
    }

    /* Constants */
	/* Segments */
    private final static String[] SEGMENTS = new String[] { "categories" };

    /* Properties */
    private Listener listener;

    public GetCategoriesRequest(Listener listener) {
        super();
        this.httpMethod = HttpMethod.GET;
        addSegmentParams(SEGMENTS);
        this.listener = listener;
    }

    @Override
    protected void onSuccess(JsonElement response) {
        try {
            Type type = new TypeToken<List<Category>>() {}.getType();
            List<Category> categories = getParser().fromJson(response, type);
            listener.onSuccess(categories);
        } catch (JsonSyntaxException e) {
            getListener().onError(e);
        }
    }

    @Override
    public ErrorListener getListener() {
        return listener;
    }
}
