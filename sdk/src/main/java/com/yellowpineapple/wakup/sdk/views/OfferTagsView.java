package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.models.Offer;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by agutierrez on 30/8/16.
 */
public class OfferTagsView extends FlowLayout {

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
        int padding = getResources().getDimensionPixelOffset(R.dimen.wk_tags_padding);
        setPadding(padding, padding, padding, padding);
        for (final String tag: offer.getTags()) {
            TextView tagButton = new TextView(getContext());
            tagButton.setBackgroundResource(R.drawable.wk_tag_bg);
            tagButton.setTextColor(getResources().getColor(R.color.wk_tag_text));
            tagButton.setText(String.format("#%s", tag));
            tagButton.setAllCaps(false);
            tagButton.setClickable(true);
            tagButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTagSelectedListener != null) {
                        onTagSelectedListener.onTagSelected(tag);
                    }
                }
            });
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(padding, padding, padding, padding);

            addView(tagButton, layoutParams);
        }
    }

    public void setOnTagSelectedListener(OnTagSelectedListener onTagSelectedListener) {
        this.onTagSelectedListener = onTagSelectedListener;
    }
}
