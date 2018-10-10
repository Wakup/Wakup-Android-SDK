package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;

public class CompanyDetail implements Serializable {

    private int id;
    private String name;
    private RemoteImage logo;
    private int offerCount;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RemoteImage getLogo() {
        return logo;
    }

    public int getOfferCount() {
        return offerCount;
    }
}
