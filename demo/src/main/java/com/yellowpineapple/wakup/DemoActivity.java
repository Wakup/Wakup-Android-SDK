package com.yellowpineapple.wakup;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.View;

import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.WakupOptions;
import com.yellowpineapple.wakup.sdk.activities.LocationActivity;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.widgets.MapWidget;
import com.yellowpineapple.wakup.sdk.widgets.OfferCarouselWidget;
import com.yellowpineapple.wakup.sdk.widgets.Widget;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DemoActivity extends LocationActivity implements Widget.OnRetryListener {

    OfferCarouselWidget carouselWidget;
    MapWidget mapWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        final Wakup wakup = Wakup.instance(this);
        // Wakup
        wakup.setup(
                new WakupOptions(getString(R.string.wakup_api_key)).
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
        carouselWidget = (OfferCarouselWidget) findViewById(R.id.offersCarousel);
        carouselWidget.setOnRetryListener(this);
        mapWidget = (MapWidget) findViewById(R.id.mapWidget);
        mapWidget.setOnRetryListener(this);
        loadNearestOffers();
    }

    void loadNearestOffers() {
        carouselWidget.setLoading(true);
        mapWidget.setLoading(true);
        getLastKnownLocation(new LocationListener() {
            @Override
            public void onLocationSuccess(Location location) {
                carouselWidget.loadOffers(location);
                mapWidget.loadNearestOffer(location);
            }

            @Override
            public void onLocationError(Exception exception) {
                carouselWidget.loadOffers(null);
                mapWidget.loadNearestOffer(null);
            }
        });
    }

    /* Include custom Wakup fonts */
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onRetry() {
        // Force ask again
        PersistenceHandler.getSharedInstance(this).setLocationAsked(false);
        PersistenceHandler.getSharedInstance(this).setLocationPermissionAsked(false);
        loadNearestOffers();
    }
}
