package com.yellowpineapple.wakup.sdk.communications;

import android.content.Context;
import android.os.Handler;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.yellowpineapple.wakup.sdk.utils.Ln;

import java.util.HashMap;
import java.util.Map;

class DefaultRequestLauncher implements RequestLauncher {
	
	private Context context;

	private RequestQueue queue;
	
	/** Array of active requests */
	private Map<Request, com.android.volley.Request> activeRequests = new HashMap<>();

	/* PUBLIC API */

    DefaultRequestLauncher(Context context) {
		this.context = context;
        queue = Volley.newRequestQueue(context);
	}

	@Override
	public void launchRequest(Request request) {
		Ln.v("Starting request: %s", request.getClass().getName());
		// Check if request is already on queue
		if (!activeRequests.containsKey(request)) {
			// Include request in execution queue
			executeRequest(request);
		} else {
			Ln.w("Request already on queue. Ignoring...");
		}
	}

	@Override
	public void cancelRequest(Request request) {
		// Cancel request by calling linked Http client method
		if (activeRequests.containsKey(request)) {
			com.android.volley.Request volleyRequest = activeRequests.get(request);
			volleyRequest.cancel();
			activeRequests.remove(request);
			Ln.v("Request canceled");
		} else {
			Ln.v("Could not cancel request, not currently active");
		}
	}
	
	@Override
	public Context getContext() {
		return context;
	}

	/* PRIVATE METHODS */
	/** Starts request execution */
	private void executeRequest(final Request request) {

		com.android.volley.Request volleyRequest = request.getRequest();

		// Include request in active requests map
		activeRequests.put(request, volleyRequest);
		request.addOnRequestFinishListener(new Request.OnRequestFinishListener() {
			@Override
			public void onRequestFinish() {
				activeRequests.remove(request);
			}
		});

		// Launch request
		if (!request.isDummy()) {
			queue.add(volleyRequest);
		} else {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (!request.isCanceled()) {
						requestEnded(request);
						request.onResponseProcess("");
					}
				}
			}, 1000);
		}
	}

	private void requestEnded(Request request) {
		activeRequests.remove(request);
	}
}
