package com.yellowpineapple.wakup.sdk.communications.requests;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.yellowpineapple.wakup.sdk.communications.RESTJSONRequest;

public abstract class BaseRequest extends RESTJSONRequest {

    public static int FIRST_PAGE = 0;
    protected static int LOCATED_RESULTS_PER_PAGE = 50;
    public static int RESULTS_PER_PAGE = 30;

    private String baseUrl;

    public BaseRequest() {
        super();
    }

    @Override
	public String getBaseURL() {
		return baseUrl;
	}

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
	protected void onResponseProcess(JsonElement response) {
        onSuccess(response);
    }

    protected abstract void onSuccess(JsonElement response);

    public boolean isHttpResponseStatusValid(int httpResponseStatusCode) {
        return httpResponseStatusCode >= 200 && httpResponseStatusCode < 400;
    }

    protected void addPagination(int page, int perPage) {
        addParam("page", page);
        addParam("perPage", perPage);
    }

    protected Gson getParser() {
        return new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
                .setDateFormat("yyyy-MM-dd")
                .create();
    }

}