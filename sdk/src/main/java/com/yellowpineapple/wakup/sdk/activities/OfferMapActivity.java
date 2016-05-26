package com.yellowpineapple.wakup.sdk.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.communications.requests.OfferListRequestListener;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.Store;
import com.yellowpineapple.wakup.sdk.utils.ImageOptions;
import com.yellowpineapple.wakup.sdk.utils.IntentBuilder;
import com.yellowpineapple.wakup.sdk.views.OfferMapInfoView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OfferMapActivity
        extends ParentActivity
        implements OnMapReadyCallback, GoogleMap.OnCameraChangeListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowClickListener {


    public final static String OFFERS_EXTRA = "offers";
    public final static String OFFER_EXTRA = "offer";
    public final static String LOCATION_EXTRA = "location";

    List<Offer> offers;
    Location location;
    Offer offer = null;

    boolean singleOffer = false;
    boolean shouldCenterMap = true;

    MapFragment mapFragment;

    Map<Marker, Offer> markersHash;

    List<Integer> displayedStores = new ArrayList<>();
    List<String> preloadedCompanies = new ArrayList<>();

    Location lastRequestLocation = null;
    private final static int NEW_REQUEST_DISTANCE_METERS = 500;

    GoogleMap googleMap;
    Timer timer = new Timer();

    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wk_activity_offers_map);
        injectExtras();
        injectViews();
    }

    void afterViews() {
        if (offer != null) {
            setSubtitle(offer.getCompany().getName());
            offers = new ArrayList<>();
            offers.add(offer);
            singleOffer = true;
        }
        preloadCompanyLogos(offers);
        mapFragment.getMapAsync(this);

    }

    void preloadCompanyLogos(List<Offer> offers) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        for (Offer offer : offers) {
            String logoURL = offer.getCompany().getLogo().getUrl();
            if (!preloadedCompanies.contains(logoURL)) {
                imageLoader.loadImage(offer.getCompany().getLogo().getUrl(), ImageOptions.get(), new SimpleImageLoadingListener());
                preloadedCompanies.add(logoURL);
            }
        }
    }

    /* OnMapReadyCallback */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.markersHash = new HashMap<>();
        googleMap.setInfoWindowAdapter(this);
        // Show more offers after user navigation when not in single mode
        if (!singleOffer) {
            googleMap.setOnCameraChangeListener(this);
            googleMap.setOnInfoWindowClickListener(this);
        }
        displayInMap(offers);

        getLastKnownLocation(new LocationListener() {
            @Override
            public void onLocationSuccess(Location location) {
                if (ActivityCompat.checkSelfPermission(OfferMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OfferMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }

            @Override
            public void onLocationError(Exception exception) {}

        });

    }

    private void displayInMap(List<Offer> offers) {
        if (offers != null) {
            for (Offer offer : offers) {
                if (offer.hasLocation() && !displayedStores.contains(offer.getStore().getId())) {
                    Store store = offer.getStore();
                    final Marker storeMarker = googleMap.addMarker(
                            new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(offer.getCategory().getIconResId()))
                                    .position(new LatLng(store.getLatitude(), store.getLongitude()))
                                    .title(offer.getCompany().getName())
                                    .snippet(store.getAddress()));
                    markersHash.put(storeMarker, offer);
                    displayedStores.add(offer.getStore().getId());
                }
            }
            if (shouldCenterMap) {
                centerMap(googleMap, new ArrayList<>(markersHash.keySet()));
            }
        }
    }

    private void centerMap(final GoogleMap googleMap, final List<Marker> markers) {
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                final GoogleMap.CancelableCallback callback = new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        if (markers.size() == 1) {
                            markers.get(0).showInfoWindow();
                        }
                    }

                    @Override
                    public void onCancel() {}
                };

                if (location != null) {
                    // Zoom and center to display both store marker and my location
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : markers) {
                        builder.include(marker.getPosition());
                    }
                    builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
                    LatLngBounds bounds = builder.build();
                    // offset from edges of the map in pixels
                    int padding = Math.round(getResources().getDimension(singleOffer ? R.dimen.wk_map_padding : R.dimen.wk_map_padding_multiple));
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    googleMap.animateCamera(cu, new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            if (singleOffer) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLng(markers.get(0).getPosition()), callback);
                            }
                        }

                        @Override
                        public void onCancel() {}
                    });
                } else {
                    if (markers.size() == 1) {
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(markers.get(0).getPosition()));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), callback);
                    }
                }
                shouldCenterMap = false;
            }
        });
    }

    // Map Event Listeners

    @Override
    public void onCameraChange(final CameraPosition cameraPosition) {
        // Check last request location to see if update is necessary
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                LatLng mapCenter = cameraPosition.target;
                //Convert LatLng to Location
                Location newLocation = new Location("MapCenter");
                newLocation.setLatitude(mapCenter.latitude);
                newLocation.setLongitude(mapCenter.longitude);
                if (isUpdateRequired(newLocation)) {
                    loadOffersByLocation(newLocation);
                }
            }
        }, 1000);
    }

    private boolean isUpdateRequired(Location newLocation) {
        return lastRequestLocation == null || lastRequestLocation.distanceTo(newLocation) > NEW_REQUEST_DISTANCE_METERS;
    }

    void loadOffersByLocation(final Location location) {
        this.lastRequestLocation = location;
        mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                setLoading(true);
                getRequestClient().findLocatedOffers(location, null, new OfferListRequestListener() {
                    @Override
                    public void onSuccess(List<Offer> offers) {
                        preloadCompanyLogos(offers);
                        displayInMap(offers);
                        setLoading(false);
                    }

                    @Override
                    public void onError(Exception exception) {
                        setLoading(false);
                        displayErrorDialog(exception);
                    }
                });
            }
        });
    }

    @Override
     public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Offer offer = markersHash.get(marker);
        OfferMapInfoView view = new OfferMapInfoView(this);
        view.setClickable(!singleOffer);
        view.setOffer(offer, location);
        return view;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Offer offer = markersHash.get(marker);
        showOfferDetail(offer, location);
    }

    private void injectExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras!= null) {
            if (extras.containsKey(OFFERS_EXTRA)) {
                offers = (List<Offer> ) extras.getSerializable(OFFERS_EXTRA);
            }
            if (extras.containsKey(OFFER_EXTRA)) {
                offer = (Offer) extras.getSerializable(OFFER_EXTRA);
            }
            if (extras.containsKey(LOCATION_EXTRA)) {
                location = extras.getParcelable(LOCATION_EXTRA);
            }
        }
    }

    @Override
    protected void injectViews() {
        super.injectViews();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment));
        afterViews();
    }

    // Builder

    public static Builder intent(Context context) {
        return new Builder(context);
    }

    public static class Builder extends IntentBuilder<OfferMapActivity> {

        public Builder(Context context) {
            super(OfferMapActivity.class, context);
        }

        public Builder offer(Offer offer) {
            getIntent().putExtra(OFFER_EXTRA, offer);
            return this;
        }

        public Builder offers(List<Offer> offers) {
            getIntent().putExtra(OFFERS_EXTRA, (Serializable) offers);
            return this;
        }

        public Builder location(Location location) {
            getIntent().putExtra(LOCATION_EXTRA, location);
            return this;
        }
    }
}
