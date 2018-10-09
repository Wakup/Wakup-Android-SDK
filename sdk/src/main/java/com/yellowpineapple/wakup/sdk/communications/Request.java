package com.yellowpineapple.wakup.sdk.communications;

import java.util.List;
import java.util.Map;

public interface Request {

	/* Inner classes */
    /**
     *  Object that will handle request error
     */
    public interface ErrorListener {
        public void onError(Exception exception);
    }
    /**
     * Object that will handle request success
     */
    public interface DefaultListener extends ErrorListener{
        public void onSuccess();
    }
    
    public interface OnRequestFinishListener {
    	public void onRequestFinish();
    }

    /**
     * Available methods for request sending
     */
    public enum HttpMethod {
        GET, POST, DELETE, PUT
    }
    
    ErrorListener getListener();

    /**
     * Put a value in request params to be included in request
     * @param key Key to identify parameter
     * @param value Value for parameter
     */
    void addParam(String key, Object value);
    
    /**
     * Put a value in request params to be included in request
     * @param param TwinRequestParam parameter to include
     */
    void addParam(RequestParam param);
    
    /**
     * Name of the request. Used to identify the method to call depending on request type
     * @return Request name
     */
	String getName();
	
	/**
	 * List of parameters included to the request, maintaining original order.
	 * @return Request parameters
	 */
	List<RequestParam> getParams();
	
	/**
	 * Sets the request launcher to notify launch or cancel request events
	 * @param requestLauncher
	 */
	void setRequestLauncher(RequestLauncher requestLauncher);
	
	/**
	 * request launcher to notify launch or cancel request events
	 * @return
	 */
	RequestLauncher getRequestLauncher(); 
	
	/**
	 * Obtains the complete target URL. In case of GET requests, parameter will be already included. 
	 * @return Complete target URL
	 */
	String getURL();

	/**
	 * 
	 * @return
	 */
    Boolean isCanceled();

    /**
     * Method that enqueue the request execution
     */
    void launch();

    /**
     * Cancels the execution of the request
     */
    void cancel();

    /**
     * HTTP method to execute the request
     * @return HTTP Method
     */
    HttpMethod getHttpMethod();
    
    /**
     * Obtains the encoding used for the request content. Default value is UTF8
     * @return Request encoding charset
     */
    String getEncoding();
    
    /**
     * Method called when the request obtains a response
     * @param response
     */
    void onResponseProcess(String response);
    
    /**
     * Method called when an error occurs while processing the request
     * @param exception
     */
    void onRequestError(Exception exception);
    
    /**
     * Content for the body of the request 
     * @return Request body content
     */
    String getBodyContent();
    
    /**
     * Type for the content body
     * @return String defining type for the body content
     */
    String getContentType();

    void addHeader(String name, String value);

    /**
     * Obtains the headers for the request
     * @return
     */
    Map<String, String> getHeaders();

    /**
     * Method that define if the request should be executed or if its execution should be only simulated
     * @return
     */
    boolean isDummy();
    
    /**
     * Method that defines if a HTTP response status code is valid. If the response is false, the system will return 'Connection error' message.
     * This method will only be called when the status code is not 200, so it is not necessary to handle it. Default response is 'false'.
     * @param httpResponseStatusCode
     * @return true if the code is valid, false otherwise
     */
    boolean isHttpResponseStatusValid(int httpResponseStatusCode);
    
    /**
     * Adds a listener to be notified when the request is finished, not depending of its result
     * @param listener
     */
    void addOnRequestFinishListener(OnRequestFinishListener listener);

    /**
     * Obtains the volley request associated with the TwinRequest
     * @return Volley request
     */
    com.android.volley.Request getRequest();
	
}
