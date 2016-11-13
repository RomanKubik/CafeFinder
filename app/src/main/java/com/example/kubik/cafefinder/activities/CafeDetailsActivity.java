package com.example.kubik.cafefinder.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.widget.TextView;

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

import butterknife.BindView;

/**
 * Displays all information about selected place.
 * Show how to get there, using google maps api.
 * Created by Kubik on 11/3/16.
 */

public class CafeDetailsActivity extends BaseCafeActivity implements OnMapReadyCallback {


    @BindView(R.id.map)
    MapFragment mMapFragment;

    @BindView(R.id.tv_cafe_info_name)
    TextView nTvCafeName;

    private String mPlaceId;

    private Place mCafeDetails;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_detailes_activity);

        mContext = this;

        getExtras();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpHeight = (int) (displayMetrics.heightPixels / displayMetrics.density);


        Log.d("MyTag", String.valueOf(displayMetrics.heightPixels));
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
