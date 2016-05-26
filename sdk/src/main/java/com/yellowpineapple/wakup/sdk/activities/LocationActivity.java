package com.yellowpineapple.wakup.sdk.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.yellowpineapple.wakup.sdk.R;
import com.yellowpineapple.wakup.sdk.utils.Ln;

public abstract class LocationActivity extends AppCompatActivity {

    protected class LocationException extends Exception {
        protected LocationException(String message) {
            super(message);
        }
    }
    protected interface LocationListener {
        void onLocationSuccess(Location location);
        void onLocationError(Exception exception);
    }

    GoogleApiClient googleApiClient = null;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
    }

    /* Google API Service */

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }


    private LocationListener locationListener = null;
    protected void getLastKnownLocation(final LocationListener listener) {
        if (googleApiClient.isConnected()) {
            checkLocationSettings(listener);
        } else {
            googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                @Override
                public void onConnected(Bundle bundle) {
                    checkLocationSettings(listener);
                }

                @Override
                public void onConnectionSuspended(int i) {
                    listener.onLocationError(new LocationException("Could not obtain location: Connection suspended"));
                }
            });
            googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(ConnectionResult connectionResult) {
                    if (mResolvingError) {
                        // If connection to Google Services failed, try to obtain location directly from provider
                        Toast.makeText(getApplicationContext(), "Could not obtain location: Connection to Google API Services failed", Toast.LENGTH_SHORT).show();
                        finish();
                    } else if (connectionResult.hasResolution()) {
                        try {
                            mResolvingError = true;
                            connectionResult.startResolutionForResult(LocationActivity.this, REQUEST_RESOLVE_ERROR);
                        } catch (IntentSender.SendIntentException e) {
                            // There was an error with the resolution intent. Try again.
                            googleApiClient.connect();
                        }
                    } else {
                        // Show dialog using GooglePlayServicesUtil.getErrorDialog()
                        showErrorDialog(connectionResult.getErrorCode());
                        mResolvingError = true;
                    }
                }
            });
            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    protected static final int NOTIFY_PERMISSION_REQUEST = 0x2;
    protected final static int PERMISSION_REQUEST_LOCATION = 0x3;

    /**
     * Checks current device location settings and opens a dialog to request enabling location
     * when needed
     */
    private void checkLocationSettings(final LocationListener locationListener) {
        this.locationListener = locationListener;

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. Now it is needed to check if the
                        // user the granted specific permissions (for Android +6)
                        checkLocationPermissions(locationListener);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Check if it has been already asked
                            if (isLocationAsked()) {
                                locationListener.onLocationError(new LocationException("Location disabled"));
                            } else {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(LocationActivity.this, REQUEST_CHECK_SETTINGS);
                            }
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            Ln.e(e);
                            locationListener.onLocationError(e);
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        locationListener.onLocationError(new LocationException("Location disabled"));
                        break;
                }
            }
        });
    }

    void checkLocationPermissions(final LocationListener locationListener) {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Check if it has been already asked
            if (isLocationPermissionAsked()) {
                locationListener.onLocationError(new LocationException("Location permission denied"));
            } else {

                this.locationListener = locationListener;
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user
                    // After the user sees the explanation, try again to request the permission.
                    Intent intent = ModalTextActivity.intent(this,
                            getString(R.string.wk_location_permission_request)).getIntent();
                    startActivityForResult(intent, NOTIFY_PERMISSION_REQUEST);

                } else {
                    requestLocationPermission();
                }
            }
        } else {
            // Permission is granted
            loadLocation(googleApiClient, locationListener);
        }
    }

    void requestLocationPermission() {
        setLocationPermissionAsked(true);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_REQUEST_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are wk_empty_logo.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted
                    loadLocation(googleApiClient, locationListener);
                } else {
                    locationListener.onLocationError(new Exception("Permission denied"));
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        loadLocation(googleApiClient, locationListener);
                        break;
                    case Activity.RESULT_CANCELED:
                        locationListener.onLocationError(new LocationException("Location disabled"));
                        break;
                }
                locationListener = null;
                break;
            case NOTIFY_PERMISSION_REQUEST:
                requestLocationPermission();
                break;
        }
    }

    private void loadLocation(final GoogleApiClient googleApiClient, final LocationListener listener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationSettings(listener);
            return;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null) {
            listener.onLocationSuccess(lastLocation);
        } else {
            // If we don't have a last location yet, request location updates
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, LocationRequest.create(), new com.google.android.gms.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
                    listener.onLocationSuccess(location);
                }
            });
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        if (!isFinishing()) {
            mResolvingError = false;
            googleApiClient.connect();
        }
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((LocationActivity)getActivity()).onDialogDismissed();
        }
    }

    /**
     * Checks if the user has been already requested to enable location in this session
     * @return true if the user has been already asked to enable location
     */
    public abstract boolean isLocationAsked();

    /**
     * Set whether the user has been already asked or not to enable device location.
     * This value should be stored for the current session.
     */
    public abstract void setLocationAsked(boolean locationAsked);

    /**
     * Checks if the user has been already requested to give location permissions to the
     * application in this session
     * @return true if the user has been already asked to give permissions
     */
    public abstract boolean isLocationPermissionAsked();

    /**
     * Set whether the user has been already asked or not to enable location permissions for the
     * application. This value should be stored for the current session.
     */
    public abstract void setLocationPermissionAsked(boolean permissionAsked);
}
