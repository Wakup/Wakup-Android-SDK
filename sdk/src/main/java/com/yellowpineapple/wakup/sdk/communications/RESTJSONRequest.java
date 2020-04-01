package com.yellowpineapple.wakup.sdk.communications;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.yellowpineapple.wakup.sdk.utils.Ln;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class RESTJSONRequest extends RESTRequest {

	private final static String BODY_CONTENT_TYPE = "application/json; charset=utf-8";

	@Override
	public String getContentType() {
		return BODY_CONTENT_TYPE;
	}

	@Override
	public void onResponseProcess(String response) {
		if (!isCanceled()) {
            Ln.d("OUTPUT: %s", response);
			try {
                JsonElement jsonElement = JsonNull.INSTANCE;
				if (response != null && response.trim().length() > 0) {
                    JsonParser parser = new JsonParser();
                    jsonElement = parser.parse(response);
				}
				onResponseProcess(jsonElement);
			} catch (JsonSyntaxException e) {
				Ln.e(e);
				onRequestError(e);
			}
			notifyFinishListeners();
		}
	}
	
	protected abstract void onResponseProcess(JsonElement response);

    @Override
    public String getBodyContent() {
        return serializeBodyParams(getParams()).toString();
    }

    @Override
	public com.android.volley.Request getRequest() {
		Ln.i("Launching request: %s", getURL());
		return new StringRequest(getRequestMethod(), getURL(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						if (!isCanceled()) {
							Ln.i("OUTPUT: %s", response);
							onResponseProcess(response);
							notifyFinishListeners();
						} else {
							Ln.i("Request cancelled, ignoring response");
						}
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						if (!isCanceled()) {
							Ln.i(error, "ERROR %s", error.getMessage());
							onRequestError(error);
							notifyFinishListeners();
						} else {
							Ln.i("Request cancelled, ignoring response");
						}
					}
				}) {

			@Override
			public byte[] getBody() throws AuthFailureError {
                try {
                    return getBodyContent().getBytes(getEncoding());
                } catch (UnsupportedEncodingException e) {
                    Ln.e(e, "Error while trying to encode body");
                    return new byte[]{};
                }
            }

			@Override
			public String getBodyContentType() {
				return getContentType();
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				return RESTJSONRequest.this.getHeaders();
			}
		};
	}

	private int getRequestMethod() {
		switch (getHttpMethod()) {
			case GET:
				return com.android.volley.Request.Method.GET;
			case DELETE:
				return com.android.volley.Request.Method.DELETE;
			case POST:
				return com.android.volley.Request.Method.POST;
			default:
				return com.android.volley.Request.Method.GET;
		}
	}

}
