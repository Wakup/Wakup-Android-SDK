package com.yellowpineapple.offers101.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yellowpineapple.offers101.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import lombok.Getter;

/**
 * Created by agutierrez on 05/02/15.
 */
@EViewGroup(R.layout.view_navbar_button)
public class NavBarButton extends FrameLayout {

    @Getter CharSequence text;
    @Getter Drawable icon;

    /* Views */
    @ViewById ImageView imgIcon;
    @ViewById TextView txtAction;

    /* Constructors */
    public NavBarButton(Context context) {
        this(context, null);
    }

    public NavBarButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavBarButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // Extract styleable attributes
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavBarButton);
            if (a.hasValue(R.styleable.NavBarButton_navText)) {
                String text = a.getString(R.styleable.NavBarButton_navText);
                setText(text);
            }
            if (a.hasValue(R.styleable.NavBarButton_navIcon)) {
                Drawable icon = a.getDrawable(R.styleable.NavBarButton_navIcon);
                setIcon(icon);
            }
            a.recycle();
        }
    }

    @AfterViews
    void afterViews() {
        // Set text
        setText(text);
        // Set image
        setIcon(icon);
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

    public void setIcon(Drawable icon) {
        this.icon = icon;
        if (imgIcon != null) {
            imgIcon.setImageDrawable(icon);
        }
    }
}