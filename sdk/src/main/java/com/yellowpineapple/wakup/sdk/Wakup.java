package com.yellowpineapple.wakup.sdk;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.yellowpineapple.wakup.sdk.activities.OfferDetailActivity;
import com.yellowpineapple.wakup.sdk.activities.OffersActivity;
import com.yellowpineapple.wakup.sdk.communications.Request;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.register.RegisterRequest;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RegistrationInfo;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.utils.Ln;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;

public class Wakup {

    private static Wakup sharedInstance = null;
    private Context context;

    private PersistenceHandler persistence;

    private static final String HOST = RequestClient.ENVIRONMENT.getUrl();

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

    public void launch() {
        if (getOptions() != null) {
            getMainActivityBuilder().start();
        } else {
            Ln.e("WAKUP NOT LAUNCHING. OPTIONS NOT PROVIDED. Call 'setup(WakupOptions) first'");
        }
    }

    public void launch(WakupOptions options) {
        persistence.setOptions(options);
        getMainActivityBuilder().start();
    }

    private IntentBuilder getMainActivityBuilder() {
        return OffersActivity.intent(context);
    }

    public void launchWithOffer(Offer offer) {
        if (getOptions() != null) {
            OfferDetailActivity.intent(context).offer(offer).start();
        } else {
            Ln.e("WAKUP NOT LAUNCHING. OPTIONS NOT PROVIDED. Call 'setup(WakupOptions) first'");
        }
    }

    public void setup(WakupOptions options) {
        persistence.setOptions(options);
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
        Uri.Builder b = Uri.parse(HOST).buildUpon();
        b.path("offers/highlighted");
        b.appendPath(getOptions().getApiKey());
        return b.build().toString();
    }

    public String getOfferReportURL(Offer offer) {
        Uri.Builder b = Uri.parse(HOST).buildUpon();
        b.appendPath("offers");
        b.appendPath(Integer.toString(offer.getId()));
        b.appendPath("report");
        if (offer.getStore() != null) {
            b.appendQueryParameter("storeId", Integer.toString(offer.getStore().getId()));
        }
        return b.build().toString();
    }

}
