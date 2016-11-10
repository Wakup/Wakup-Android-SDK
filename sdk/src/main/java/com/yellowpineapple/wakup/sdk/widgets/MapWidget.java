package com.yellowpineapple.wakup.sdk.widgets;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ViewListener;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferRequestListener;
import com.yellowpineapple.wakup.sdk.models.MapMarker;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.Store;
import com.yellowpineapple.wakup.sdk.utils.ImageOptions;
import com.yellowpineapple.wakup.sdk.views.OfferCarouselItemView;
import com.yellowpineapple.wakup.sdk.views.OfferSmallView;

import java.util.List;
import java.util.Map;

/**
 * Created by agutierrez on 3/11/16.
 */
public class MapWidget extends LinearLayout {

    Location location;

    // Views
    MapView mapView;
    TextView txtAddress;
    GoogleMap googleMap;
    Offer offer;
    Store store;


    public MapWidget(Context context) {
        super(context);
        init();
    }

    public MapWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    void init() {
        inflate(getContext(), R.layout.wk_widget_map, this);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(Bundle.EMPTY);
        mapView.onResume();
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        findViewById(R.id.rippleView).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onLocationClick();
            }
        });
    }

    public void loadNearestOffer(final Location location) {
        this.location = location;
        if (location != null) {
            RequestClient.getSharedInstance(getContext()).findNearestOffer(location, new OfferRequestListener() {
                @Override
                public void onSuccess(final Offer offer) {
                    MapWidget.this.offer = offer;
                    MapWidget.this.store = offer.getStore();
                    txtAddress.setText(store.getAddress());

                    // Run on Main Thread
                    Handler mainHandler = new Handler(getContext().getMainLooper());
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mapView.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(final GoogleMap googleMap) {

                                    MapWidget.this.googleMap = googleMap;

                                    final Marker storeMarker = googleMap.addMarker(
                                            new MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.fromResource(MapMarker.getOfferIcon(getContext(), offer)))
                                                    .position(new LatLng(store.getLatitude(), store.getLongitude())));
                                    // Center map
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(storeMarker.getPosition()));
                                    googleMap.moveCamera(CameraUpdateFactory.zoomTo(14));
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(Exception exception) {
                    displayError(getContext().getString(R.string.wk_search_error));
                }

            });

        } else {
            displayError("Location is not enabled");
        }
    }

    public void displayError(String errorMessage) {
        new AlertDialog.Builder(getContext()).setTitle(errorMessage).create();
    }

    void onLocationClick() {
        // Creates an Intent that will load a map of location
        Uri gmmIntentUri = Uri.parse(
                String.format(getContext().getString(R.string.wk_map_link_format),
                        store.getLatitude(),
                        store.getLongitude()));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        getContext().startActivity(mapIntent);
    }

}
