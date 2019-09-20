package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * Created by agutierrez on 8/11/16.
 */

public class DynamicLinesTextView extends AppCompatTextView {

    public DynamicLinesTextView(Context context) {
        super(context);
        init();
    }

    public DynamicLinesTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicLinesTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        // Ensure that the description fits allowed lines by component height
        ViewTreeObserver observer = getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int maxLines = (int) getHeight()
                        / getLineHeight();
                setMaxLines(maxLines);
                setEllipsize(TextUtils.TruncateAt.END);
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }
}
