package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yellowpineapple.wakup.sdk.R;

public class OfferActionButton extends FrameLayout {

    static final int[] SELECTED_STATE_SET = new int[] { android.R.attr.state_selected, android.R.attr.state_enabled };
    static final int[] PRESSED_STATE_SET = new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled };
    static final int[] DISABLED_STATE_SET = new int[] { - android.R.attr.state_enabled };
    static final int[] DEFAULT_STATE_SET = new int[] { android.R.attr.state_enabled };

    CharSequence text = null;
    Drawable icon = null;
    int iconColor = 0;
    Float fontSize = 0f;

    /* Views */
    ViewGroup actionView;
    ImageView imgIcon;
    TextView txtAction;

    /* Constructors */
    public OfferActionButton(Context context) {
        this(context, null);
    }

    public OfferActionButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OfferActionButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Extract styleable attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OfferActionButton);
            if (a.hasValue(R.styleable.OfferActionButton_buttonText)) {
                String text = a.getString(R.styleable.OfferActionButton_buttonText);
                this.text = text;
            }
            if (a.hasValue(R.styleable.OfferActionButton_buttonIconColor)) {
                this.iconColor = a.getColor(R.styleable.OfferActionButton_buttonIconColor,
                        ContextCompat.getColor(getContext(), R.color.wk_action_active_icon));
            }
            if (a.hasValue(R.styleable.OfferActionButton_buttonIcon)) {
                this.icon = a.getDrawable(R.styleable.OfferActionButton_buttonIcon);
            }
            if (a.hasValue(R.styleable.OfferActionButton_buttonFontSize)) {
                this.fontSize = a.getDimension(R.styleable.OfferActionButton_buttonFontSize, 0f);
            }
            a.recycle();
        }
        init();
    }

    private void init() {
        injectViews();
        // Set text
        if (text != null) setText(text);
        // Set image
        if (icon != null) setIcon(icon, iconColor);
        // Set font size
        if (fontSize > 0) setFontSize(fontSize);
    }

    private void injectViews() {
        inflate(getContext(), R.layout.wk_view_action_button, this);
        imgIcon = (ImageView) findViewById(R.id.imgIcon);
        txtAction = (TextView) findViewById(R.id.txtAction);
        actionView = (ViewGroup) findViewById(R.id.actionView);
    }

    public void setText(int textResId) {
        setText(getResources().getText(textResId));
    }

    public void setText(CharSequence text) {
        this.text = text;
        if (txtAction != null) {
            txtAction.setText(text);
        }
    }

    public void setIcon(int iconResource, int iconColor) {
        setIcon(getResources().getDrawable(iconResource), getResources().getColor(iconColor));
    }

    public void setIcon(Drawable icon, int iconColor) {
        this.icon = icon;
        if (imgIcon != null) {
            if (!isInEditMode()) {
                Drawable defaultDrawable = icon.getCurrent();
                int activeColor = iconColor != 0 ? iconColor : ContextCompat.getColor(getContext(), R.color.wk_action_active_icon);
                defaultDrawable.setColorFilter(activeColor, PorterDuff.Mode.MULTIPLY);
                defaultDrawable = drawableToBitmap(defaultDrawable);

                Drawable pressedDrawable = icon.getCurrent();
                pressedDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.wk_action_pressed_icon), PorterDuff.Mode.MULTIPLY);
                pressedDrawable = drawableToBitmap(pressedDrawable);

                Drawable disabledDrawable = icon.getCurrent();
                disabledDrawable.setColorFilter(ContextCompat.getColor(getContext(), R.color.wk_action_inactive_icon), PorterDuff.Mode.MULTIPLY);
                disabledDrawable = drawableToBitmap(disabledDrawable);

                StateListDrawable listDrawable = new StateListDrawable();
                listDrawable.addState(DISABLED_STATE_SET, disabledDrawable);
                listDrawable.addState(SELECTED_STATE_SET, pressedDrawable);
                listDrawable.addState(PRESSED_STATE_SET, pressedDrawable);
                listDrawable.addState(DEFAULT_STATE_SET, defaultDrawable);
                imgIcon.setImageDrawable(listDrawable);
            } else {
                imgIcon.setColorFilter(getResources().getColor(R.color.wk_action_active_icon));
                imgIcon.setImageDrawable(icon);
            }
        }
    }

    public void setFontSize(Float fontSize) {
        this.fontSize = fontSize;
        txtAction.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
    }

    public BitmapDrawable drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return new BitmapDrawable(getContext().getResources(), bitmap);
    }
}