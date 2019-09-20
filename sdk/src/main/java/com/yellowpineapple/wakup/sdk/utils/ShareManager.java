package com.yellowpineapple.wakup.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.activities.ParentActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by agutierrez on 26/02/15.
 */
public class ShareManager {

    public static void shareImage(ParentActivity context, Bitmap image, String fileName, String shareDialogTitle, String text) {
        Uri bmpUri = getBitmapUri(context, image, fileName);
        if (bmpUri != null) {
            // Construct a ShareIntent with link to image
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            shareIntent.setType("image/*");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            // Launch sharing dialog for image
            context.startActivity(Intent.createChooser(shareIntent, shareDialogTitle));
        } else {
            context.displayErrorDialog(context.getString(R.string.wk_share_offer_error));
        }
    }

    private static Uri getBitmapUri(Context context, Bitmap bitmap, String fileName) {
        // Store image to default external storage directory
        String bitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap,fileName, null);
        return Uri.parse(bitmapPath);
    }
}
