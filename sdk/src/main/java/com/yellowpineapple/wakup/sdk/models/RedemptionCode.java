package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;

/**
 * Created by agutierrez on 29/8/16.
 */
public class RedemptionCode implements Serializable {

    Integer totalCodes;
    Integer availableCodes;
    boolean limited;
    boolean alreadyAssigned;

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
