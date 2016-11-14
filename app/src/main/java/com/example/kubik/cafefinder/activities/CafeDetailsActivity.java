package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.fragments.WorkaroundMapFragment;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import butterknife.BindDimen;

/**
 * Displays all information about selected place.
 * Show how to get there, using google maps api.
 * Created by Kubik on 11/3/16.
 */

public class CafeDetailsActivity extends BaseCafeActivity implements OnMapReadyCallback {

    private static final int DEFAULT_CAMERA_PADDING = 250;

    private ScrollView mScrollView;
    private TextView mTvCafeName;
    private ImageView mImgCafeImage;
    private WorkaroundMapFragment mMap;

    @BindDimen(R.dimen.button_height_super_tall)
    int mCafeNameHeightPx;

    private int mScreenHeightPx;

    private String mPlaceId;

    private Place mCafeDetails;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_detailes_activity);

        initializeViews();

        mContext = this;

        getExtras();

        Log.d("MyTag", String.valueOf(mCafeNameHeightPx));
    }

    private void initializeViews() {
        if (mMap == null) {
            mMap = (WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mScrollView = (ScrollView) findViewById(R.id.scv_cafe_details);

            ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .setListener(new WorkaroundMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            mScrollView.requestDisallowInterceptTouchEvent(true);
                        }
                    });
        }

        mTvCafeName = (TextView) findViewById(R.id.tv_cafe_info_name);
        mImgCafeImage = (ImageView) findViewById(R.id.img_cafe_info);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getExtras() {
        mPlaceId = getIntent().getStringExtra(MainActivity.EXTRA_PLACE_ID);
    }

    private int getScreenHeightPx() {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return displayMetrics.heightPixels - result;
    }


    private void getDetails() {
        Places.GeoDataApi.getPlaceById(sGoogleApiClient, mPlaceId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            mCafeDetails = places.get(0);
                            setContent();
                            mMap.getMapAsync((OnMapReadyCallback) mContext);
                        } else {
                            Log.d("MyTag", "Place not found");
                        }
                    }
                });
    }

    private void setContent() {
        mScreenHeightPx = getScreenHeightPx();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.height = mScreenHeightPx - mCafeNameHeightPx;
        mMap.getView().setLayoutParams(params);

        mTvCafeName.setText(mCafeDetails.getName());

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng cafeLatLng = mCafeDetails.getLatLng();
        LatLng myLatLng = new LatLng(sLocation.getLatitude(), sLocation.getLongitude());
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(cafeLatLng);
        builder.include(myLatLng);
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_CAMERA_PADDING);
        googleMap.animateCamera(cameraUpdate);
        String marker = (String) mCafeDetails.getName();
        googleMap.addMarker(new MarkerOptions().position(cafeLatLng).title(marker));

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
        }
        googleMap.setMyLocationEnabled(true);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDetails();
    }

}
