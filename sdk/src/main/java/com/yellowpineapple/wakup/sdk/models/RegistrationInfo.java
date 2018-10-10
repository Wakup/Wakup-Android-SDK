package com.yellowpineapple.wakup.sdk.models;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.google.gson.Gson;
import com.yellowpineapple.wakup.sdk.BuildConfig;
import com.yellowpineapple.wakup.sdk.utils.Ln;

import java.util.Locale;

public class RegistrationInfo {

    /* Wakup SDK Version */
    public String sdkVersion = null;
    /*  setup parameters */
    public String appID = null;
    /* Client Application Version */
    public String appVersion = null;
    /* Android version name ("4.2.2", "5.1") */
    public String osVersion = null;
    /* Android API Level (19 for "4.2.2", etc) */
    public Integer osVersionInt = null;
    /* Current device locale ("en_US", "es_ES")*/
    public String language = null;
    /* Device Manufacturer ("samsung", "motorola") */
    public String deviceManufacturer = null;
    /* User readable device model ("Galaxy Nexus", "Moto G3") */
    public String deviceModel = null;
    /* Technical device code */
    public String deviceCode = null;
    /* Device Unique Identifier */
    public String udid = null;


    public static RegistrationInfo fromContext(Context context) {
        RegistrationInfo info = new RegistrationInfo();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            info.appVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Ln.e(e, "Could not obtain application version");
        }
        info.sdkVersion = BuildConfig.VERSION_NAME;
        info.udid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        info.osVersion = Build.VERSION.RELEASE;
        info.osVersionInt = Build.VERSION.SDK_INT;
        info.deviceManufacturer = capitalize(Build.MANUFACTURER);
        info.deviceModel = capitalize(Build.MODEL);
        info.deviceCode = capitalize(Build.DEVICE);
        info.language = Locale.getDefault().toString();
        info.appID = context.getPackageName();

        info.printLog();

        return info;
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first) || s.contains("_")) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private void printLog() {
        Ln.d("============================================");
        Ln.d("===        WAKUP Registration info       ===");
        Ln.d("============================================");
        Ln.d("App ID:          %s", appID);
        Ln.d("App Version:     %s", appVersion);
        Ln.d("SDK Version:     %s", sdkVersion);
        Ln.d("Android Version: %s (API %d)", osVersion, osVersionInt);
        Ln.d("Device:          %s %s (%s)", deviceManufacturer, deviceModel, deviceCode);
        Ln.d("UDID:            %s", udid);
        Ln.d("Locale:          %s", language);
        Ln.d("============================================");
    }

    public String toJson() {
        return new Gson().toJson(this);
    }

}
