package com.yellowpineapple.wakup;

import android.location.Location;
import android.os.Bundle;

import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.WakupOptions;
import com.yellowpineapple.wakup.sdk.activities.LocationActivity;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.Ln;

import java.util.List;

public class SplashActivity extends LocationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Wakup wakup = Wakup.instance(this);
        // Wakup
        wakup.launch(
                    new WakupOptions("075f9656-6909-4e4e-a286-3ddc562a2513").
                            country("ES").
                            defaultLocation(41.38506, 2.17340)
            );
    }
}
