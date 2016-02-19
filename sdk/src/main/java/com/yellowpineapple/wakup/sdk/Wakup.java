package com.yellowpineapple.wakup.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.yellowpineapple.wakup.sdk.activities.OffersActivity;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.register.RegisterRequest;
import com.yellowpineapple.wakup.sdk.models.RegistrationInfo;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;

public class Wakup {

    private static Wakup sharedInstance = null;
    private Context context;

    private PersistenceHandler persistence;

    private static final String BIG_OFFER_URL = "https://app.wakup.net/offers/highlighted/%s";

    private Wakup(Context context) {
        super();
        this.context = context;
        this.persistence = PersistenceHandler.getSharedInstance(context);
    }

    public static Wakup instance(Context context) {
        if (sharedInstance == null) {
            sharedInstance = new Wakup(context);
        }
        return sharedInstance;
    }

    public void launch(WakupOptions options) {
        persistence.setOptions(options);
        OffersActivity.intent(context).start();
    }

    public WakupOptions getOptions() {
        return persistence.getOptions();
    }

    public interface RegisterListener extends Request.ErrorListener {
        void onSuccess();
    }
    public void register(final RegisterListener listener) {
        final RegistrationInfo info = RegistrationInfo.fromContext(context);
        if (persistence.registrationRequired(info)) {
            RequestClient.getSharedInstance(context).register(info, new RegisterRequest.Listener() {
                @Override
                public void onSuccess(String deviceToken) {
                    persistence.setDeviceToken(deviceToken);
                    persistence.setRegistrationInfo(info);
                    listener.onSuccess();
                }

                @Override
                public void onError(Exception exception) {
                    listener.onError(exception);
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    listener.onSuccess();
                }
            });
        }

    }

    public String getBigOffer() {
        return String.format(BIG_OFFER_URL, getOptions().getApiKey());
    }

}
