package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.yellowpineapple.wakup.sdk.models.Offer;

/**
 * Created by agutierrez on 30/8/16.
 */
public class OfferTagsView extends LinearLayout {

    public interface OnTagSelectedListener {
        void onTagSelected(String tag);
    }

    Offer offer;
    OnTagSelectedListener onTagSelectedListener = null;

    public OfferTagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public OfferTagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OfferTagsView(Context context) {
        super(context);
        init();
    }

    public void init() {
        setOrientation(HORIZONTAL);
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
        removeAllViews();
        for (final String tag: offer.getTags()) {
            Button tagButton = new Button(getContext());
            tagButton.setText(String.format("#%s", tag));
            tagButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTagSelectedListener != null) {
                        onTagSelectedListener.onTagSelected(tag);
                    }
                }
            });
            addView(tagButton);
        }
    }

    public void setOnTagSelectedListener(OnTagSelectedListener onTagSelectedListener) {
        this.onTagSelectedListener = onTagSelectedListener;
    }
}
