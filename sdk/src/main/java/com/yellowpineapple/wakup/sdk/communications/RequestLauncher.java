package com.yellowpineapple.wakup.sdk.communications;

import android.content.Context;

public interface RequestLauncher {

	/**
	 * Obtains current context
	 * @return Context
	 */
	Context getContext();
	
	/**
	 * Adds a request to execution queue
	 */
	void launchRequest(Request request);

	/** 
	 * Cancels the execution of a requests, removing it from execution queue or canceling its connection if already launched
	 */
	void cancelRequest(Request request);
}
