package com.yellowpineapple.wakup.sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.yellowpineapple.wakup.sdk.WakupOptions;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.models.RegistrationInfo;
import com.yellowpineapple.wakup.sdk.models.SearchResultItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PersistenceHandler {

    private static PersistenceHandler INSTANCE = null;
    private static final String PREFS_NAME = "101OffersPref";
    private static final String KEY_USER_OFFERS = "KEY_USER_OFFERS";
    private static final String KEY_RECENT_SEARCHES = "KEY_RECENT_SEARCHES";
    private static final String KEY_SDK_OPTIONS = "KEY_SDK_OPTIONS";
    private static final String KEY_REGISTRATION_INFO = "KEY_REGISTRATION_INFO";
    private static final String KEY_DEVICE_TOKEN = "KEY_DEVICE_TOKEN";
    private static final int MAX_RECENT_SEARCHES = 10;

    private SharedPreferences preferences = null;
    private List<String> userOffers = null;
    private List<SearchResultItem> recentSearches = null;
    private WakupOptions options = null;
    private boolean locationAsked = false;
    private boolean locationPermissionAsked = false;
    private String deviceToken = null;

    Date savedOffersUpdatedAt = new Date();

    Context context;

    private PersistenceHandler(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public static PersistenceHandler getSharedInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new PersistenceHandler(context);
        }
        return INSTANCE;
    }

    private SharedPreferences getPreferences() {
        return preferences;
    }

    // User saved offers

    public List<String> getUserOffers() {
        if (userOffers == null) {
            userOffers = new ArrayList<>(getPreferences().getStringSet(KEY_USER_OFFERS, new HashSet<String>()));
        }
        return userOffers;
    }

    public boolean isSavedOffer(Offer offer) {
        return getUserOffers().contains(getOfferKey(offer));
    }

    public void saveOffer(Offer offer) {
        String offerKey = getOfferKey(offer);
        userOffers.add(offerKey);
        storeUserOffers();
    }

    public void removeSavedOffer(Offer offer) {
        String offerKey = getOfferKey(offer);
        if (userOffers.contains(offerKey)) {
            userOffers.remove(offerKey);
            storeUserOffers();
        }
    }

    private String getOfferKey(Offer offer) {
        return String.valueOf(offer.getId());
    }

    private void storeUserOffers() {
        Set<String> offerKeys = new HashSet<>();
        offerKeys.addAll(getUserOffers());
        getPreferences().edit().putStringSet(KEY_USER_OFFERS, offerKeys).commit();
        savedOffersUpdatedAt = new Date();
    }

    public boolean userOffersChanged(Date dateSince) {
        return savedOffersUpdatedAt != null && savedOffersUpdatedAt.after(dateSince);
    }

    // Recent searches

    public void addRecentSearch(SearchResultItem item) {
        if (recentSearches != null) {
            // Remove previously searched equal items
            List<SearchResultItem> itemsToRemove = new ArrayList<>();
            for (SearchResultItem storedItem : recentSearches) {
                if (storedItem.equals(item)) {
                    itemsToRemove.add(storedItem);
                }
            }
            recentSearches.removeAll(itemsToRemove);
        } else {
            recentSearches = new ArrayList<>();
        }
        recentSearches.add(0, item);
        // Remove last elements if max size exceeded
        while (recentSearches.size() > MAX_RECENT_SEARCHES) {
            recentSearches.remove(MAX_RECENT_SEARCHES);
        }
        storeRecentSearches();
    }

    public List<SearchResultItem> getRecentSearches() {
        if (recentSearches == null) {
            try {
                String json = getPreferences().getString(KEY_RECENT_SEARCHES, "[]");
                Type type = new TypeToken<List<SearchResultItem>>() {}.getType();
                recentSearches = new Gson().fromJson(new JsonParser().parse(json), type);
            } catch (Exception ex) {
                Ln.e(ex, "Error while trying to load recent searches");
                recentSearches = new ArrayList<>();
            }
        }
        return recentSearches;
    }

    public void storeRecentSearches() {
        String json;
        if (recentSearches != null) {
            json = new Gson().toJson(recentSearches);
        } else {
            json = new Gson().toJson(new ArrayList<SearchResultItem>());
        }
        getPreferences().edit().putString(KEY_RECENT_SEARCHES, json).commit();
    }

    public boolean isLocationAsked() {
        return locationAsked;
    }

    public void setLocationAsked(boolean locationAsked) {
        this.locationAsked = locationAsked;
    }

    public boolean isLocationPermissionAsked() {
        return locationPermissionAsked;
    }

    public void setLocationPermissionAsked(boolean locationPermissionAsked) {
        this.locationPermissionAsked = locationPermissionAsked;
    }

    // Setup options
    public void setOptions(WakupOptions options) {
        this.options = options;
        if (options != null) {
            String json = new Gson().toJson(options);
            getPreferences().edit().putString(KEY_SDK_OPTIONS, json).commit();
        } else {
            getPreferences().edit().remove(KEY_SDK_OPTIONS).commit();
        }
    }

    public WakupOptions getOptions() {
        if (options == null) {
            try {
                String json = getPreferences().getString(KEY_SDK_OPTIONS, null);
                if (json != null) {
                    options = new Gson().fromJson(json, WakupOptions.class);
                } else {
                    options = new WakupOptions(null);
                }
            } catch (Exception ex) {
                Ln.e(ex, "Error while trying to load SDK options");
                options = new WakupOptions(null);
            }
        }
        return options;
    }

    // Registration info
    public void setRegistrationInfo(RegistrationInfo info) {
        if (info != null) {
            String serialized = info.toJson();
            getPreferences().edit().putString(KEY_REGISTRATION_INFO, serialized).commit();
        } else {
            getPreferences().edit().remove(KEY_REGISTRATION_INFO).commit();
        }
    }

    public boolean registrationRequired(RegistrationInfo info) {
        // Registration is required if device token is wk_empty_logo or if registration info changed
        return Strings.isEmpty(getDeviceToken()) ||
                Strings.equals(
                        getPreferences().getString(KEY_REGISTRATION_INFO, null),
                        info.toJson());
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
        getPreferences().edit().putString(KEY_DEVICE_TOKEN, deviceToken).commit();
    }

    public String getDeviceToken() {
        if (Strings.isEmpty(this.deviceToken)) {
            this.deviceToken = getPreferences().getString(KEY_DEVICE_TOKEN, null);
        }
        return deviceToken;
    }
}
