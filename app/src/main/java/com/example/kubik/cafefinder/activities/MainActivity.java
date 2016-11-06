package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.DistanceSpinnerAdapter;
import com.example.kubik.cafefinder.adapters.MainCafeListAdapter;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.models.BaseCafeInfo;
import com.example.kubik.cafefinder.models.CafeList;
import com.example.kubik.cafefinder.requests.ApiClient;
import com.example.kubik.cafefinder.requests.ApiInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseCafeActivity
        implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{

    public static final String TAG = "MainActivity";

    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";

    public static final int ACCESS_LOCATION_CODE = 1001;
    public static final int ALL_PERMISSIONS_CODE = 1000;

    private int mSearchRadius = 250;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private CafeList mCafeList;

    @BindView(R.id.rv_main_cafe_list)
    RecyclerView mRecyclerView;
    private MainCafeListAdapter mCafeListAdapter;

    @BindView(R.id.tb_main_activity)
    Toolbar mToolbar;

    @BindView(R.id.sp_main_activity)
    Spinner mSpinner;

    @BindString(R.string.search_options)
    String mSearchOptions;

    private boolean mIsFirstStart = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.main_activity);
        super.onCreate(savedInstanceState);

        setToolbar();

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

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        final List<String> list = new ArrayList<>();
        Collections.addAll(list, getResources().getStringArray(R.array.spinner_items));

        final DistanceSpinnerAdapter adapter = new DistanceSpinnerAdapter(this, list);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mSearchRadius = 250;
                        break;
                    case 1:
                        mSearchRadius = 500;
                        break;
                    case 2:
                        mSearchRadius = 1000;
                        break;
                    case 3:
                        mSearchRadius = 2000;
                        break;
                }
                if (!mIsFirstStart) {
                    getCafeList();
                } else {
                    mIsFirstStart = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String location = mLastLocation.getLatitude() + "," + mLastLocation.getLongitude();
        Call<CafeList> call = apiService.getNearbyPlaces(location, mSearchRadius, mSearchOptions,
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        List<BaseCafeInfo> cafes = mCafeList.getResults();
        mCafeListAdapter = new MainCafeListAdapter(cafes, this, mLastLocation);
        mRecyclerView.setAdapter(mCafeListAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
        mCafeListAdapter.setOnItemClickListener(new MainCafeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Start new activity with selected cafe details
                BaseCafeInfo cafe = mCafeList.getResults().get(position);
                String placeId = cafe.getPlaceId();
                Intent intent = new Intent(getApplicationContext(), CafeDetailsActivity.class);
                intent.putExtra(EXTRA_PLACE_ID, placeId);
                startActivity(intent);
                Log.d(TAG, String.valueOf(position));
            }
        });
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
