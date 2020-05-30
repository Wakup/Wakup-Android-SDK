package com.yellowpineapple.wakup.sdk.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.Wakup;
import com.yellowpineapple.wakup.sdk.communications.RequestClient;
import com.yellowpineapple.wakup.sdk.models.Offer;
import com.yellowpineapple.wakup.sdk.utils.Ln;
import com.yellowpineapple.wakup.sdk.utils.PersistenceHandler;
import com.yellowpineapple.wakup.sdk.utils.ShareManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public abstract class ParentActivity extends LocationActivity {

    RequestClient requestClient = null;
    Wakup wakup = null;

    Toolbar toolbar = null;

    private PersistenceHandler persistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        requestClient = RequestClient.getSharedInstance(this);
        persistence = PersistenceHandler.getSharedInstance(this);
        wakup = Wakup.instance(this);

        // Fix portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = super.onOptionsItemSelected(item);
        if (handled) {
            return true;
        }
        int itemId_ = item.getItemId();
        if (itemId_ == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        slideOutTransition();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public PersistenceHandler getPersistence() {
        return persistence;
    }

    public RequestClient getRequestClient() {
        return requestClient;
    }

    public Wakup getWakup() {
        return wakup;
    }

    @Override
    public void setTitle(final CharSequence title) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
            ab.setDisplayShowTitleEnabled(true);
        } else {
            ParentActivity.super.setTitle(title);
        }
    }

    public void setSubtitle(final CharSequence subtitle) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void injectViews() {
        setupToolbar();
    }

    /* Dialogs */

    ProgressDialog mDialog = null;
    AlertDialog alert = null;

    public void displayLoadingDialog() {
        displayLoadingDialog(getString(R.string.wk_loading_default));
    }

    public void displayLoadingDialog(int textId) {
        displayLoadingDialog(getString(textId));
    }

    public void displayLoadingDialog(String text) {
        closeLoadingDialog();
        mDialog = ProgressDialog.show(this, "", text, true);
    }

    public void closeLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mDialog = null;
    }

    public void closeDialog() {
        closeLoadingDialog();
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
        alert = null;
    }

    public void displayAlertDialog(int titleTextId, int messageTextId, int buttonTextId) {
        displayAlertDialog(titleTextId, messageTextId, buttonTextId, null);
    }

    public void displayAlertDialog(int titleTextId, int messageTextId, int buttonTextId, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageTextId)
                .setTitle(titleTextId)
                .setCancelable(true)
                .setPositiveButton(buttonTextId, listener);
        displayDialog(builder);
    }

    public void displayErrorDialog(Throwable throwable) {
        if (throwable instanceof IOException) {
            this.displayErrorDialog(getString(R.string.wk_connection_error_message));
        } else {
            this.displayErrorDialog(throwable.getLocalizedMessage());
        }
    }

    public void displayErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(R.string.wk_error_message_title)
                .setCancelable(true)
                .setPositiveButton(R.string.wk_error_message_button, null);
        displayDialog(builder);
    }

    public void displayConfirmDialog(int titleTextId, int messageTextId, int positiveButtonTextId, int negativeButtonTextId, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageTextId).setTitle(titleTextId).setCancelable(true).setPositiveButton(positiveButtonTextId, positiveListener).setNegativeButton(negativeButtonTextId, null);
        displayDialog(builder);
    }

    public void displayConfirmDialog(int titleTextId, String messageText, int positiveButtonTextId, int negativeButtonTextId, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageText).setTitle(titleTextId).setCancelable(true).setPositiveButton(positiveButtonTextId, positiveListener).setNegativeButton(negativeButtonTextId, null);
        displayDialog(builder);
    }

    public void displayConfirmDialog(int titleTextId, int messageTextId, int positiveButtonTextId, int negativeButtonTextId, View customView, DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(messageTextId).setTitle(titleTextId).setCancelable(true).setPositiveButton(positiveButtonTextId, positiveListener).setNegativeButton(negativeButtonTextId, null).setView(customView);
        displayDialog(builder);
    }

    protected void displayDialog(final AlertDialog.Builder builder) {
        if (!isFinishing()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Close previous dialog (if exists)
                    closeDialog();
                    alert = builder.create();
                    alert.show();
                }

            });
        }
    }

    protected void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        if (alert != null) {
            alert.cancel();
        }
        if (mDialog != null) {
            mDialog.cancel();
        }
        super.onDestroy();
    }

    // Actions

    protected void showOfferDetail(Offer offer, Location currentLocation) {
        OfferDetailActivity.intent(this).offer(offer).location(currentLocation).start();
        slideInTransition();
    }

    protected void displayInMap(Offer offer, Location currentLocation) {
        OfferMapActivity.intent(this).offer(offer).location(currentLocation).start();
        slideInTransition();
    }

    protected boolean isSavedOffer(Offer offer) {
        PersistenceHandler persistence = getPersistence();
        return persistence.isSavedOffer(offer);
    }

    protected void saveOffer(Offer offer) {
        PersistenceHandler persistence = getPersistence();
        persistence.saveOffer(offer);
    }

    protected void removeSavedOffer(Offer offer) {
        PersistenceHandler persistence = getPersistence();
        persistence.removeSavedOffer(offer);
    }

    protected void openOfferLink(Offer offer) {
        if (offer.hasLink()) {
            Uri url = Uri.parse(offer.getLink());
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
    }

    /* Activity transitions */

    protected void slideInTransition() {
        overridePendingTransition(R.anim.wk_slide_in_right, R.anim.wk_fade_back);
    }

    protected void slideOutTransition() {
        overridePendingTransition(R.anim.wk_fade_forward, R.anim.wk_slide_out_right);
    }

    /* Report offer error */

    void reportOffer(final Offer offer) {
        String reportURL = getWakup().getOfferReportURL(offer);
        WebViewActivity.intent(this).
                url(reportURL).
                title(getString(R.string.wk_activity_report)).
                linksInBrowser(false).
                start();
        slideInTransition();
    }

    /* Offer sharing */

    Offer sharedOffer = null;
    void shareOffer(final Offer offer) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            this.sharedOffer = offer;
            requestStoragePermission();
            return;
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    Bitmap bitmap = Glide.with(getBaseContext()).asBitmap().load(offer.getImage().getUrl()).submit().get();
                    String shareTitle = getString(R.string.wk_share_offer_title);
                    String text = getString(R.string.wk_share_offer_subject);
                    text = text.replace("{id}", Integer.toString(offer.getId()));
                    text = text.replace("{company}", offer.getCompany().getName());
                    text = text.replace("{description}", offer.getShortDescription());
                    text = text.replace("{fullDescription}", offer.getDescription());
                    text = text.replace("{shortOffer}", offer.getShortOffer());
                    String fileName = String.format(Locale.ENGLISH, "wakup_offer_%d.png", offer.getId());
                    ShareManager.shareImage(ParentActivity.this, bitmap, fileName, shareTitle, text);
                } catch (Exception ex) {
                    Ln.e(ex);
                    displayErrorDialog(getString(R.string.wk_share_offer_error));
                }
            }
        });}

    protected final static int PERMISSION_REQUEST_STORAGE = 0x90;
    void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    shareOffer(sharedOffer);
                }
            }
        }
    }

    public void setLoading(boolean loading) {
        try {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_spinner);
            if(loading)
                progressBar.setVisibility(ProgressBar.VISIBLE);
            else
                progressBar.setVisibility(ProgressBar.GONE);
        } catch(NoSuchFieldError e) {
            Log.d("NOTPRESENT", "Progress bar not present");
        }
    }

    protected boolean isCompaniesVisible() {
        return getWakup().getOptions().isCompaniesVisible();
    }
}
