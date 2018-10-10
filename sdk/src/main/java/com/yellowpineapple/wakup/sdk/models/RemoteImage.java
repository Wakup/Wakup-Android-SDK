package com.yellowpineapple.wakup.sdk.models;

import java.io.Serializable;

public class RemoteImage implements Serializable {

    private int height;
    private int width;
    private String rgbColor;
    private String url;

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public String getRgbColor() {
        return rgbColor;
    }

    public String getUrl() {
        return url;
    }
}
