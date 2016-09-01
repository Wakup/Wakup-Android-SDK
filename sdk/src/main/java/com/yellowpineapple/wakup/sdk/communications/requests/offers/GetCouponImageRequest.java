package com.yellowpineapple.wakup.sdk.communications.requests.offers;

import android.net.Uri;

import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RemoteImage;

/**
 * Created by agutierrez on 1/9/16.
 */
public class GetCouponImageRequest {

    static final String SIZE = "200";
    String url;

    public GetCouponImageRequest(Offer offer, String format, RequestClient.Environment environment, String userToken) {

        Uri.Builder b = Uri.parse(environment.getUrl()).buildUpon();
        b.appendPath("offers");
        b.appendPath(Integer.toString(offer.getId()));
        b.appendPath("code");
        b.appendPath(format);
        b.appendPath(SIZE); // Width
        b.appendPath(SIZE); // Height

        b.appendQueryParameter("userToken", userToken);

        url = b.build().toString();
    }

    public String getUrl() {
        return url;
    }

    public RemoteImage getRemoteImage() {
        return new CodeRemoteImage(url);
    }

    private class CodeRemoteImage extends RemoteImage {

        String url;


        public CodeRemoteImage(String url) {
            super();
            this.url = url;
        }

        @Override
        public int getHeight() {
            return 200;
        }

        @Override
        public int getWidth() {
            return 200;
        }

        @Override
        public String getRgbColor() {
            return "FFFFFF";
        }

        @Override
        public String getUrl() {
            return url;
        }
    }
}
