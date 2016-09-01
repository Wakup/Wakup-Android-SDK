package com.yellowpineapple.wakup.sdk.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.yellowpineapple.wakup.sdk.models.RemoteImage;
import com.yellowpineapple.wakup.sdk.utils.ImageOptions;

public class RemoteImageView extends AspectKeepFrameLayout {

    RemoteImage image;
    ImageView backImageView;
    ImageView imageView;
    ImageLoader imageLoader;

    interface ImageLoadListener {
        void onImageLoad(Bitmap loadedImage);
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
        backImageView = new ImageView(getContext());
        imageView = new ImageView(getContext());
        if (!isInEditMode()) {
            addView(backImageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(imageView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        imageLoader = ImageLoader.getInstance();
    }

    public void setImageSync(RemoteImage image) {
        if (!isInEditMode()) {
            if (image != null) {
                this.setVirtualSize(image.getHeight(), image.getWidth());
                int color = Color.parseColor(String.format("#%s", image.getRgbColor()));
                Drawable placeholder = new ColorDrawable(color);
                Bitmap bitmap = ImageLoader.getInstance().loadImageSync(image.getUrl(), ImageOptions.get());
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageDrawable(placeholder);
                }
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
                        public void onImageLoad(Bitmap loadedImage) {
                            loadImage(image, new BitmapDrawable(getResources(), loadedImage));
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
        SimpleImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (listener != null) {
                    listener.onImageLoad(loadedImage);
                }
            }
        };
        backImageView.setImageDrawable(placeholder);
        imageLoader.displayImage(image.getUrl(), imageView,
                ImageOptions.builder()
                .showImageOnLoading(placeholder)
                .showImageOnFail(placeholder)
                .displayer(new FadeInBitmapDisplayer(300, true, true, false))
                .build(), imageLoadingListener);
    }
}
