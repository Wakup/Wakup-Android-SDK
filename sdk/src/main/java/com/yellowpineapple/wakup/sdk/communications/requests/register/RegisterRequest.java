package com.yellowpineapple.wakup.sdk.communications.requests.register;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.requests.BaseRequest;
import com.yellowpineapple.wakup.sdk.models.RegistrationInfo;

public class RegisterRequest extends BaseRequest {
	
	public interface Listener extends Request.ErrorListener {
		void onSuccess(String deviceToken);
	}
	
	/* Constants */
	/* Segments */
	private final static String REGISTER_SEGMENT = "register";
	/* Parameters */
	private final static String PLATFORM_KEY = "platform";
	private final static String PLATFORM_VALUE = "android";
    private final static String APP_ID_KEY = "appId";
	private final static String UDID_KEY = "deviceId";
	private final static String APP_VERSION_KEY = "appVersion";
	private final static String SDK_VERSION_KEY = "sdkVersion";
	private final static String OS_NAME = "osName";
	private final static String OS_VERSION_KEY = "osVersion";
	private final static String OS_VERSION_CODE_KEY = "osVersionCode";
	private final static String MANUFACTURER_KEY = "deviceManufacturer";
	private final static String DEVICE_MODEL_KEY = "deviceModel";
	private final static String DEVICE_CODE_KEY = "deviceCode";
	private final static String LOCALE_KEY = "locale";

	/* Response fields */
	private final static String RESPONSE_DEVICE_TOKEN_KEY = "userToken";

	/* Properties */
	private Listener listener;
	
	public RegisterRequest(RegistrationInfo registrationInfo, Listener listener) {
		super();
		this.listener = listener;
		this.httpMethod = HttpMethod.POST;
		// Segments
		addSegmentParam(REGISTER_SEGMENT);
		// Parameters
		addParam(PLATFORM_KEY, PLATFORM_VALUE);
        addParam(UDID_KEY, registrationInfo.udid);
		addParam(APP_ID_KEY, registrationInfo.appID);
		addParam(APP_VERSION_KEY, registrationInfo.appVersion);
        addParam(SDK_VERSION_KEY, registrationInfo.sdkVersion);
		addParam(OS_NAME, "Android");
        addParam(OS_VERSION_KEY, registrationInfo.osVersion);
        addParam(OS_VERSION_CODE_KEY, registrationInfo.osVersionInt);
        addParam(MANUFACTURER_KEY, registrationInfo.deviceManufacturer);
        addParam(DEVICE_MODEL_KEY, registrationInfo.deviceModel);
        addParam(DEVICE_CODE_KEY, registrationInfo.deviceCode);
        addParam(LOCALE_KEY, registrationInfo.language);
	}
	
	@Override
	protected void onSuccess(JsonElement response) {
		try {
            String deviceToken = response.getAsJsonObject().
                    getAsJsonPrimitive(RESPONSE_DEVICE_TOKEN_KEY).
                    getAsString();
			listener.onSuccess(deviceToken);
		} catch (JsonSyntaxException e) {
			getListener().onError(e);
		}
	}
	
	public Listener getListener() {
		return listener;
	}

}
