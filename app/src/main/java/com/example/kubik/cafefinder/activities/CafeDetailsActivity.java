package com.example.kubik.cafefinder.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.ImagePagerAdapter;
import com.example.kubik.cafefinder.customViews.ReviewView;
import com.example.kubik.cafefinder.fragments.WorkaroundMapFragment;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.helpers.PhotoTask;
import com.example.kubik.cafefinder.models.CafeInfo;
import com.example.kubik.cafefinder.models.CafeReview;
import com.example.kubik.cafefinder.requests.ApiClient;
import com.example.kubik.cafefinder.requests.ApiInterface;
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
import butterknife.BindDrawable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Displays all information about selected place.
 * Show how to get there, using google maps api.
 * Created by Kubik on 11/3/16.
 */

public class CafeDetailsActivity extends BaseCafeActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final int DEFAULT_CAMERA_PADDING = 250;

    private static final float DEFAULT_MINIMUM_CAFE_RATING = 1;

    private ScrollView mScrollView;
    private Toolbar mToolbar;
    private TextView mTvCafeName;
    private WorkaroundMapFragment mMap;
    private ViewPager mImagePager;
    private TextView mTvCafeRate;
    private TextView mTvCafeAddress;
    private TextView mTvCafePhone;
    private TextView mTvCafeLink;
    private LinearLayout mLlReviewContainer;
    private ImageView mImgLike;

    private ImagePagerAdapter mImagePagerAdapter;

    @BindDimen(R.dimen.button_height_super_tall)
    int mCafeNameHeightPx;
    @BindDrawable(R.drawable.heart)
    Drawable mNotFavouriteHeart;
    @BindDrawable(R.drawable.filled_heart)
    Drawable mFavouriteHeart;

    private String mPlaceId;

    private Place mCafeDetails;
    private CafeInfo mCafeInfo;
    private List<Bitmap> mPhotoList = new ArrayList<>();
    private List<CafeReview> mReviewList = new ArrayList<>();

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

        mImgLike = (ImageView) findViewById(R.id.img_like);
        mImgLike.setOnClickListener(this);
        mTvCafeRate = (TextView) findViewById(R.id.tv_cafe_info_rate);
        mTvCafeAddress = (TextView) findViewById(R.id.tv_cafe_info_address);
        mTvCafeAddress.setOnClickListener(this);
        mTvCafePhone = (TextView) findViewById(R.id.tv_cafe_info_phone);
        mTvCafeLink = (TextView) findViewById(R.id.tv_cafe_info_link);
        mLlReviewContainer = (LinearLayout) findViewById(R.id.ll_review_container);
    }

    private void getExtras() {
        mPlaceId = getIntent().getStringExtra(MainActivity.EXTRA_PLACE_ID);
    }

    private void loadCafeDetails() {
        Places.GeoDataApi.getPlaceById(sGoogleApiClient, mPlaceId)
                .setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(@NonNull PlaceBuffer places) {
                        if (places.getStatus().isSuccess() && places.getCount() > 0) {
                            mCafeDetails = places.get(0);
                            showBaseInformation();
                            mMap.getMapAsync((OnMapReadyCallback) mContext);
                        } else {
                            Log.d("MyTag", "Place not found");
                        }
                    }
                });
    }

    private void loadPhotoList() {
        new PhotoTask(mImagePager.getWidth(), mImagePager.getHeight(), sGoogleApiClient) {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected void onPostExecute(AttributedPhoto attributedPhoto) {
                if (attributedPhoto != null) {
                    if (attributedPhoto.getBitmapList().size() == 0) {
                        mPhotoList.add(BitmapFactory.decodeResource(getResources(), R.drawable.logo_cafe));
                    }
                    for (Bitmap btm : attributedPhoto.getBitmapList()) {
                        mPhotoList.add(btm);
                    }
                    mImagePagerAdapter.notifyDataSetChanged();
                }
            }
        }.execute(mPlaceId);
    }

    private void loadReviews() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<CafeInfo> call = apiService.getCafeReviews(mPlaceId, ApiUrlBuilder.getApiKey());
        call.enqueue(new Callback<CafeInfo>() {
            @Override
            public void onResponse(Call<CafeInfo> call, Response<CafeInfo> response) {
                mCafeInfo = response.body();
                mReviewList = mCafeInfo.getResult().getReviews();
                showReviews();
            }

            @Override
            public void onFailure(Call<CafeInfo> call, Throwable t) {
                Log.d("MY_TAG", t.toString());
            }
        });

    }

    private void showAll() {
        showBaseInformation();
        showReviews();
        mImagePagerAdapter.updateImageList(mPhotoList);
        mImagePagerAdapter.notifyDataSetChanged();
        mMap.getMapAsync((OnMapReadyCallback) mContext);
    }

    private void showBaseInformation() {
        int mScreenHeightPx = getScreenHeightPx();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        params.height = mScreenHeightPx - mCafeNameHeightPx;
        if (mMap.getView() != null) {
            mMap.getView().setLayoutParams(params);
        }

        mTvCafeName.setText(mCafeDetails.getName());
        mToolbar.setTitle(null);

        String rating = String.valueOf(DEFAULT_MINIMUM_CAFE_RATING);
        if (mCafeDetails.getRating() >= DEFAULT_MINIMUM_CAFE_RATING) {
            rating = String.valueOf(mCafeDetails.getRating());
        }
        mTvCafeRate.setText(rating);
        mTvCafeAddress.setText(mCafeDetails.getAddress());
        if (mCafeDetails.getPhoneNumber().length() > 0) {
            mTvCafePhone.setText(mCafeDetails.getPhoneNumber());
            mTvCafePhone.setLinksClickable(true);
        } else {
            mTvCafePhone.setText(R.string.phone_number_error);
            mTvCafePhone.setLinksClickable(false);
        }

        if (mCafeDetails.getWebsiteUri() != null) {
            mTvCafeLink.setText(mCafeDetails.getWebsiteUri().toString());
            mTvCafeLink.setLinksClickable(true);
        } else {
            mTvCafeLink.setText(R.string.link_error);
            mTvCafeLink.setLinksClickable(false);
        }
    }

    private void showReviews() {
        if (mReviewList.size() != 0) {
            for (CafeReview review : mReviewList) {
                ReviewView reviewView = new ReviewView(this);
                reviewView.setComponents(review.getAuthorName(), (float) review.getRating()
                        , review.getText());
                mLlReviewContainer.addView(reviewView);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (sDbHelper.isFavouritePlace(sProfile, mPlaceId)) {
            mImgLike.setImageDrawable(mFavouriteHeart);
            mCafeDetails = sDbHelper.getPlaceInfo(sProfile, mPlaceId);
            mPhotoList = sDbHelper.getImages(sProfile, mPlaceId);
            mReviewList = sDbHelper.getReviews(sProfile, mPlaceId);
            showAll();
            Log.d("MyTag", "Loaded from cache");
        } else {
            mImgLike.setImageDrawable(mNotFavouriteHeart);
            loadCafeDetails();
            loadPhotoList();
            loadReviews();
            Log.d("MyTag", "Loaded from internet");
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cafe_info_address:
                mScrollView.fullScroll(ScrollView.FOCUS_UP);
                break;
            case R.id.img_like:
                if (sDbHelper.isFavouritePlace(sProfile, mPlaceId)) {
                    sDbHelper.removeFavouriteCafe(sProfile, mPlaceId);
                    mImgLike.setImageDrawable(mNotFavouriteHeart);
                    Log.d("MyTag", "Removed from favourite");
                } else {
                    sDbHelper.addFavouriteCafe(sProfile, mPlaceId, mCafeDetails, mPhotoList
                            , mReviewList);
                    mImgLike.setImageDrawable(mFavouriteHeart);
                    Log.d("MyTag", "Added to favourite");
                }
                break;
        }
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

}
