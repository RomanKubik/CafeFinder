package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.ImagePagerAdapter;
import com.example.kubik.cafefinder.fragments.WorkaroundMapFragment;
import com.example.kubik.cafefinder.helpers.PhotoTask;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;

/**
 * Displays all information about selected place.
 * Show how to get there, using google maps api.
 * Created by Kubik on 11/3/16.
 */

public class CafeDetailsActivity extends BaseCafeActivity implements OnMapReadyCallback {

    private static final int DEFAULT_CAMERA_PADDING = 250;

    private List<Bitmap> mPhotoList = new ArrayList<>();

    private ScrollView mScrollView;
    private Toolbar mToolbar;
    private TextView mTvCafeName;
    private WorkaroundMapFragment mMap;
    private ViewPager mImagePager;
    private TextView mTvCafeRate;

    private PagerAdapter mImagePagerAdapter;

    @BindDimen(R.dimen.button_height_super_tall)
    int mCafeNameHeightPx;

    private String mPlaceId;

    private Place mCafeDetails;

    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cafe_detailes_activity);

        mContext = this;

        initializeViews();

        getExtras();

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

        mToolbar = (Toolbar) findViewById(R.id.tb_cafe_details_activity);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        mImagePagerAdapter = new ImagePagerAdapter(mPhotoList, this);

        mImagePager = (ViewPager) findViewById(R.id.vp_image);
        mImagePager.setAdapter(mImagePagerAdapter);

        mTvCafeRate = (TextView) findViewById(R.id.tv_cafe_info_rate);

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
        int mScreenHeightPx = getScreenHeightPx();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.height = mScreenHeightPx - mCafeNameHeightPx;
        if (mMap.getView() != null) {
            mMap.getView().setLayoutParams(params);
        }

        mTvCafeName.setText(mCafeDetails.getName());
        mToolbar.setTitle(null);

        mTvCafeRate.setText(String.valueOf(mCafeDetails.getRating()));

    }

    private void getPhotoList() {
        new PhotoTask(mImagePager.getWidth(), mImagePager.getHeight(), sGoogleApiClient) {
            @Override
            protected void onPreExecute() {
                // Display a temporary image to show while bitmap is loading.
                mPhotoList.add(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.logo_cafe));
                mImagePagerAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    //Remove first template image
                    if (mPhotoList.size() > 0) {
                        mPhotoList.remove(0);

                    }
                    // Photo has been loaded, display it.
                    for (Bitmap btm : attributedPhoto.getBitmapList()) {
                        mPhotoList.add(btm);
                    }

                    mImagePagerAdapter.notifyDataSetChanged();
                }
            }
        }.execute(mPlaceId);
    }





    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getDetails();
        getPhotoList();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Get cafe location
        LatLng cafeLatLng = mCafeDetails.getLatLng();
        //Get my location
        LatLng myLatLng = new LatLng(sLocation.getLatitude(), sLocation.getLongitude());
        //Set display area
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(cafeLatLng);
        builder.include(myLatLng);
        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, DEFAULT_CAMERA_PADDING);
        //Move camera to selected area
        googleMap.animateCamera(cameraUpdate);
        //Add marker to map
        String marker = (String) mCafeDetails.getName();
        googleMap.addMarker(new MarkerOptions().position(cafeLatLng).title(marker));
        //Show my current location
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            getPermissions();
        }
        googleMap.setMyLocationEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
