package com.yellowpineapple.wakup.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class IntentBuilder<T extends Activity> {

    private Context context;
    private Intent intent;

    public IntentBuilder(Class<T> tClass, Context context) {
        this.intent = new Intent(context, tClass);
        this.context = context;
    }

    public void start() {
        context.startActivity(intent);
    }

    public Intent getIntent() {
        return intent;
    }

    protected Context getContext() {
        return context;
    }
}
