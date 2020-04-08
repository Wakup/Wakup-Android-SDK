package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.target.Target;
import com.yellowpineapple.wakup.sdk.models.RemoteImage;
import com.yellowpineapple.wakup.sdk.utils.Ln;

import java.net.URL;

public class RemoteImageView extends AspectKeepFrameLayout {

    RemoteImage image;
    ImageView backImageView;
    ImageView imageView;
    RequestManager glide;

    interface ImageLoadListener {
        void onImageLoad(Drawable loadedImage);
    }

    public RemoteImageView(Context context) {
        super(context);
        init(null, 0);
    }

    public RemoteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    void init(AttributeSet attrs, int defStyle) {
        glide = Glide.with(getContext());
        backImageView = new ImageView(getContext());
        imageView = new ImageView(getContext());
        if (!isInEditMode()) {
            addView(backImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    public void setImageSync(RemoteImage image) {
        if (!isInEditMode()) {
            if (image != null) {
                this.setVirtualSize(image.getHeight(), image.getWidth());
                int color = Color.parseColor(String.format("#%s", image.getRgbColor()));
                Drawable placeholder = new ColorDrawable(color);
                imageView.setImageDrawable(placeholder);
                loadImageSync(image, placeholder);
            }
        }
    }

    public void setImage(RemoteImage image) {
        setImage(image, null);
    }

    public void setImage(final RemoteImage image, final RemoteImage thumbnail) {
        this.image = image;
        if (!isInEditMode()) {
            if (image != null) {
                this.setVirtualSize(image.getHeight(), image.getWidth());
                int color = Color.parseColor(String.format("#%s", image.getRgbColor()));
                Drawable placeholder = new ColorDrawable(color);
                imageView.setImageDrawable(placeholder);
                if (thumbnail != null) {
                    loadImage(thumbnail, placeholder, new ImageLoadListener() {
                        @Override
                        public void onImageLoad(final Drawable loadedImage) {
                            imageView.setImageDrawable(loadedImage);
                            // Run in main handler
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    loadImage(image, loadedImage);
                                }
                            });
                        }
                    });
                } else {
                    loadImage(image, placeholder);
                }
            }
        }
    }

    void loadImage(RemoteImage image, Drawable placeholder) {
        loadImage(image, placeholder, null);
    }

    void loadImage(RemoteImage image, Drawable placeholder, final ImageLoadListener listener) {
        RequestFutureTarget<Drawable> glideListener = new RequestFutureTarget<Drawable>(image.getWidth(), image.getWidth()) {
            @Override
            public synchronized boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return super.onLoadFailed(e, model, target, isFirstResource);
            }

            @Override
            public synchronized boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                if (listener != null) {
                    listener.onImageLoad(resource);
                    return true;
                } else {
                    return super.onResourceReady(resource, model, target, dataSource, isFirstResource);
                }
            }
        };
        glide.load(image.getUrl()).placeholder(placeholder).listener(glideListener).into(this.imageView);

    }

    void loadImageSync(RemoteImage image, Drawable placeholder) {
        try {
            imageView.setImageDrawable(placeholder);
            URL url = new URL(image.getUrl());
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        } catch (Exception ex) {
            Ln.e(ex);
        }
    }
}
