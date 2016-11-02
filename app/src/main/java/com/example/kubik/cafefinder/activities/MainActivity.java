package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.MainCafeListAdapter;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.models.BaseCafeInfo;
import com.example.kubik.cafefinder.models.CafeList;
import com.example.kubik.cafefinder.requests.ApiClient;
import com.example.kubik.cafefinder.requests.ApiInterface;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseCafeActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    public static final String TAG = "MainActivity";

    public static final int ACCESS_LOCATION_CODE = 1001;
    public static final int ALL_PERMISSIONS_CODE = 1000;

    private GoogleSignInAccount mGoogleAccount;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private CafeList mCafeList;

    private MainCafeListAdapter mCafeListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        getIntents();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();

        checkPermissions();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        mGoogleApiClient.connect();
        super.onStart();

    }

    private void getIntents() {

        /*mGoogleAccount = (GoogleSignInAccount) getIntent()
                .getSerializableExtra(LoginActivity.EXTRA_ACCOUNT);*/
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
        getLastLocation();
        getCafeList();
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            getPermission(ACCESS_LOCATION_CODE);
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()) + "; "
                    + String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
        }
    }

    private void getCafeList() {
        getLastLocation();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String location = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
        Call<CafeList> call = apiService.getNearbyPlaces(location, 500, "cafe",
                ApiUrlBuilder.getApiKey());

        call.enqueue(new Callback<CafeList>() {
            @Override
            public void onResponse(Call<CafeList> call, Response<CafeList> response) {
                mCafeList = response.body();
                showList();
            }

            @Override
            public void onFailure(Call<CafeList> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    private void showList() {
        Location location = new Location("");
        location.setLongitude(mLastLocation.getLongitude());
        location.setLatitude(mLastLocation.getLatitude());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_main_cafe_list);
        List<BaseCafeInfo> cafes = mCafeList.getResults();
        mCafeListAdapter = new MainCafeListAdapter(cafes, this, location);
        recyclerView.setAdapter(mCafeListAdapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        showPlacesList();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }
}
