package com.yellowpineapple.wakup.sdk.models;

public class CompanyDetail extends Company {

    private RemoteImage logo;
    private int offerCount;

    public RemoteImage getLogo() {
        return logo;
    }

    public int getOfferCount() {
        return offerCount;
    }
}
