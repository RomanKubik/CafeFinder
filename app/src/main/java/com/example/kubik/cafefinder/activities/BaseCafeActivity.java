package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.helpers.ConnectionHelper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import butterknife.ButterKnife;

/**
 * Base class for all activities. This class binds views initialized in child activity.
 * Gets permissions, sets up GoogleApiClient and gets last known location, checks internet access.
 */

public class BaseCafeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private static final int ACCESS_LOCATION_CODE = 1001;


    protected static GoogleSignInAccount sSignInAccount;
    protected static GoogleApiClient sGoogleApiClient;
    protected static Location sLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupGoogleApi();
        getPermissions();

        ButterKnife.bind(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        sGoogleApiClient.connect();
        try {
            if (!ConnectionHelper.isConnected(this)) {
                Toast.makeText(this, R.string.not_connected_msg, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void setupGoogleApi() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        sGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();
    }

    private void getLocation() {
        if (sLocation != null) {
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) {
            getPermissions();
        }
        sLocation = LocationServices.FusedLocationApi.getLastLocation(sGoogleApiClient);
        if (sLocation == null) {
            Log.d("MyTag", "Location == null");
        }
    }

    protected void getPermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                , ACCESS_LOCATION_CODE);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.not_connected_msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
