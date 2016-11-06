package com.example.kubik.cafefinder.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kubik.cafefinder.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Displays all information about selected place.
 * Show how to get there, using google maps api.
 * Created by Kubik on 11/3/16.
 */

public class CafeDetailsActivity extends BaseCafeActivity implements OnMapReadyCallback {

    private String mPlaceId;

    private Place mCafeDetails;

    private MapFragment mMapFragment;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_detailes_activity);

        mContext = this;

        getExtras();

        mMapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getExtras() {
        mPlaceId = getIntent().getStringExtra(MainActivity.EXTRA_PLACE_ID);
    }



    private void getDetails() {
        Places.GeoDataApi.getPlaceById(sGoogleApiClient, mPlaceId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            mCafeDetails = places.get(0);
                            mMapFragment.getMapAsync((OnMapReadyCallback) mContext);
                        } else {
                            Log.d("CafeDetailsActivity", "Place not found");
                        }
                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = mCafeDetails.getLatLng();
        String marker = (String) mCafeDetails.getName();
        googleMap.addMarker(new MarkerOptions().position(latLng).title(marker));
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDetails();
    }

}
