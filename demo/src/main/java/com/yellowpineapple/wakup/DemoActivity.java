package com.yellowpineapple.wakup;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.WakupOptions;
import com.yellowpineapple.wakup.sdk.activities.LocationActivity;
import com.yellowpineapple.wakup.sdk.widgets.OfferCarouselWidget;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DemoActivity extends LocationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        final Wakup wakup = Wakup.instance(this);
        // Wakup
        wakup.setup(
                new WakupOptions("075f9656-6909-4e4e-a286-3ddc562a2513").
                        country("ES").
                        defaultLocation(41.38506, 2.17340).
                        showBackInRoot(true)
        );

        findViewById(R.id.btnOffers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wakup.launch();
            }
        });
        final OfferCarouselWidget carouselWidget = (OfferCarouselWidget) findViewById(R.id.offersCarousel);
        getLastKnownLocation(new LocationListener() {
            @Override
            public void onLocationSuccess(Location location) {
                carouselWidget.loadOffers(location);
            }

            @Override
            public void onLocationError(Exception exception) {
                carouselWidget.displayError("Location is not enabled");
            }
        });
    }

    /* Include custom Wakup fonts */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
