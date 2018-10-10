package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;
import java.util.List;

public class RedemptionCodeDetail implements Serializable{

    private String code;
    private String displayCode;
    private List<String> formats;

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
