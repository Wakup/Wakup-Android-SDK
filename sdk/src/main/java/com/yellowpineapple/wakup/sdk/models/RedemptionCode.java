package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;

public class RedemptionCode implements Serializable {

    private Integer totalCodes;
    private Integer availableCodes;
    private boolean limited;
    private boolean alreadyAssigned;

    public Integer getTotalCodes() {
        return totalCodes;
    }

    public Integer getAvailableCodes() {
        return availableCodes;
    }

    public boolean isLimited() {
        return limited;
    }

    public boolean isAlreadyAssigned() {
        return alreadyAssigned;
    }
}
