package com.yellowpineapple.wakup.sdk;

import android.location.Location;

import com.yellowpineapple.wakup.sdk.models.Category;
import com.yellowpineapple.wakup.sdk.models.MapMarker;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by agutierrez on 18/1/16.
 */
public class WakupOptions {

    /** Wakup Client API Key */
    private String apiKey = null;

    /** Country for address search  */
    private String country = "ES";

    /** Default Coordinates for offers search when geo-location is disabled */
    private double defaultLatitude = 40.41678;
    private double defaultLongitude = -3.70379;

    private boolean showBackInRoot = false;

    final static List<Category> DEFAULT_CATEGORIES = Arrays.asList(
            new Category(R.string.wk_category_leisure, R.drawable.wk_cat_leisure, R.color.wk_search_cat_leisure,  "leisure"),
            new Category(R.string.wk_category_restaurants, R.drawable.wk_cat_restaurants, R.color.wk_search_cat_restaurants, "restaurants"),
            new Category(R.string.wk_category_services, R.drawable.wk_cat_services, R.color.wk_search_cat_services, "services"),
            new Category(R.string.wk_category_shopping, R.drawable.wk_cat_shopping, R.color.wk_search_cat_shopping, "shopping")
    );
    List<Category> categories = DEFAULT_CATEGORIES;

    final static List<MapMarker> DEFAULT_MAP_MARKERS = Arrays.asList(
            new MapMarker(R.drawable.wk_pin_leisure,     "leisure"),
            new MapMarker(R.drawable.wk_pin_restaurants, "restaurants"),
            new MapMarker(R.drawable.wk_pin_services,    "services"),
            new MapMarker(R.drawable.wk_pin_shopping,    "shopping"),
            new MapMarker(R.drawable.wk_pin_unknown)
    );

    List<MapMarker> mapMarkers = DEFAULT_MAP_MARKERS;

    /**
     * Creates an Options object to setup the Wakup SDK
     *
     * @param apiKey Wakup client API Key
     */
    public WakupOptions(String apiKey) {
        super();
        this.apiKey = apiKey;
    }

    /**
     * Specifies the country to restrict geo-search.
     * Default is set to Spain ("ES").
     *
     * @param country ISO Country code (https://en.wikipedia.org/wiki/ISO_3166-1)
     * @return options instance to allow in-line setup
     */
    public WakupOptions country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Set the default location to use when device location can not be accessed.
     * Default is set to Madrid (40.41678, -3.70379).
     *
     * @param latitude Location latitude
     * @param longitude Location longitude
     * @return options instance to allow in-line setup
     */
    public WakupOptions defaultLocation(double latitude, double longitude) {
        this.defaultLatitude = latitude;
        this.defaultLongitude = longitude;
        return this;
    }

    /**
     * Set the categories available to filter in search results.
     *
     * Each category contains a list of tags that will be used to filter search results.
     *
     * @param categories Desired search filter categories
     * @return options instance to allow in-line setup
     */
    public WakupOptions categories(List<Category> categories) {
        this.categories = categories;
        return this;
    }

    /**
     * Set the customized markers to display offers in Map View
     * Each marker is associated with an array of tags and will represent all the offers that
     * contains any of the selected tags.
     * If no tags are included in the Marker, it will be used as default when the offer does not
     * match with other markers.
     *
     * @param mapMarkers List of markers to represent offers in the MapView
     * @return options instance to allow in-line setup
     */
    public WakupOptions mapMarkers(List<MapMarker> mapMarkers) {
        this.mapMarkers = mapMarkers;
        return this;
    }

    /**
     * Defines if the back navigation button should be displayed in the Toolbar of the Root activity.
     * Default is false.
     * @param showBackInRoot true to display back navigation button in Root activity
     * @return  options instance to allow in-line setup
     */
    public WakupOptions showBackInRoot(boolean showBackInRoot) {
        this.showBackInRoot = showBackInRoot;
        return this;
    }

    /* Getters */

    public String getApiKey() {
        return apiKey;
    }

    public String getCountryCode() {
        return country;
    }

    public String getCountryName() {
        Locale loc = new Locale("", country);
        return loc.getDisplayCountry();
    }

    public Location getDefaultLocation() {
        Location location = new Location("Default");
        location.setLatitude(defaultLatitude);
        location.setLongitude(defaultLongitude);
        return location;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<MapMarker> getMapMarkers() {
        return mapMarkers;
    }

    public boolean showBackInRoot() {
        return showBackInRoot;
    }
}
