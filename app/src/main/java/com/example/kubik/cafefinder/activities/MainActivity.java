package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseCafeActivity
        implements GoogleApiClient.OnConnectionFailedListener{

    public static final String TAG = "MainActivity";

    public static final int ACCESS_LOCATION_CODE = 1001;
    public static final int ALL_PERMISSIONS_CODE = 1000;

    private GoogleSignInAccount mGoogleAccount;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getIntents();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .enableAutoManage(this, this)
                .build();

        checkPermissions();

        showPlacesList();
    }


    private void getIntents() {
        mGoogleAccount = (GoogleSignInAccount) getIntent()
                .getSerializableExtra(LoginActivity.EXTRA_ACCOUNT);
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            getPermission(ALL_PERMISSIONS_CODE);
        }
    }

    private void getPermission(int permissionCode) {
        switch (permissionCode) {
            case ACCESS_LOCATION_CODE:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , ACCESS_LOCATION_CODE);
                break;

            default:
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                        , ACCESS_LOCATION_CODE);
                break;
        }
    }


    private void showPlacesList() {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void click(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermission(ACCESS_LOCATION_CODE);
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + "; "
                    + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        }
    }
}
