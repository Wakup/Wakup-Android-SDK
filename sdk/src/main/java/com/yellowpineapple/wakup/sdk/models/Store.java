package com.yellowpineapple.wakup.sdk.models;

import android.location.Location;

import java.io.Serializable;

public class Store implements Serializable {

    private int id;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

    public Location getLocation() {
        Location location = null;
        if (latitude != 0 || longitude != 0) {
            location = new Location(name);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }
        return location;
    }

    public static int LOCATION_INVALID = -1;
    public int getDistance(Location location) {
        int distance = LOCATION_INVALID;
        Location storeLocation = getLocation();
        if (storeLocation != null && location != null) {
            distance = Math.round(location.distanceTo(storeLocation));
        }
        return distance;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
