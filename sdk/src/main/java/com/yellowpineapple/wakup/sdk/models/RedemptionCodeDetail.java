package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by agutierrez on 29/8/16.
 */
public class RedemptionCodeDetail implements Serializable{

    String code;
    String displayCode;
    List<String> formats;

    public String getCode() {
        return code;
    }

    public String getDisplayCode() {
        return displayCode;
    }

    public List<String> getFormats() {
        return formats;
    }
}
