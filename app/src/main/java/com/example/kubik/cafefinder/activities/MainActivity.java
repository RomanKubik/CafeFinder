package com.example.kubik.cafefinder.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.DistanceSpinnerAdapter;
import com.example.kubik.cafefinder.adapters.MainCafeListAdapter;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.helpers.EndlessRecyclerViewScrollListener;
import com.example.kubik.cafefinder.models.BaseCafeInfo;
import com.example.kubik.cafefinder.models.CafeList;
import com.example.kubik.cafefinder.requests.ApiClient;
import com.example.kubik.cafefinder.requests.ApiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindString;
import butterknife.BindView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseDrawerCafeActivity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";

    private int mSearchRadius = 250;

    private CafeList mCafeList = new CafeList();

    private LinearLayoutManager mLayoutManager;
    private List<BaseCafeInfo> mCafes = new ArrayList<>();
    private MainCafeListAdapter mCafeListAdapter;
    private EndlessRecyclerViewScrollListener mScrollListener;

    @BindView(R.id.rv_main_cafe_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.sp_main_activity)
    Spinner mSpinner;
    @BindView(R.id.img_favourite_list)
    ImageView mImgFavourite;

    @BindString(R.string.search_options)
    String mSearchOptions;
    @BindString(R.string.rankby_options)
    String mRankByOptions;


    @BindDrawable(R.drawable.heart)
    Drawable mNormal;
    @BindDrawable(R.drawable.filled_heart)
    Drawable mFavourite;

    private boolean mIsFirstStart = true;
    private boolean mIsFavouriteList = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (sLocation == null)
            super.getLocation();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setRecyclerView();
        setToolbar();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        if (mIsFavouriteList) {
            mCafeList.setResults(sDbHelper.getFavouriteCafeList(sProfile));
            showList();
        }
        super.onStart();
    }

    private void setRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (!mIsFavouriteList)
                    loadNextPage();
            }
        };
        mCafeListAdapter = new MainCafeListAdapter(mCafes, this, sLocation);
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mCafeListAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mCafeListAdapter.setOnItemClickListener(new MainCafeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Start new activity with selected cafe details
                BaseCafeInfo cafe = mCafes.get(position);
                String placeId = cafe.getPlaceId();
                Intent intent = new Intent(getApplicationContext(), CafeDetailsActivity.class);
                intent.putExtra(EXTRA_PLACE_ID, placeId);
                startActivity(intent);
                Log.d(TAG, String.valueOf(position));
            }
        });
    }

    private void setToolbar() {
        initToolbar(R.id.tb_main_activity);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        mImgFavourite.setOnClickListener(this);

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
                if (mIsFavouriteList) {
                    mIsFavouriteList = false;
                    mImgFavourite.setImageDrawable(mNormal);
                }
                if (!mIsFirstStart) {
                    loadCafeList();
                } else {
                    mIsFirstStart = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void loadCafeList() {
        mCafes.clear();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        String location = sLocation.getLatitude() + "," + sLocation.getLongitude();
        Call<CafeList> call = apiService.getNearbyPlaces(location, mSearchRadius, mRankByOptions
                , mSearchOptions, ApiUrlBuilder.getApiKey());

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

    private void loadNextPage() {
        if (mCafeList.getNextPageToken() != null) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            String location = sLocation.getLatitude() + "," + sLocation.getLongitude();

            Call<CafeList> call = apiService.getNextPagePlaces(location, mSearchRadius
                    , mCafeList.getNextPageToken(), ApiUrlBuilder.getApiKey());

            call.enqueue(new Callback<CafeList>() {
                @Override
                public void onResponse(Call<CafeList> call, Response<CafeList> response) {
                    mCafeList = response.body();
                    showList();
                }

                @Override
                public void onFailure(Call<CafeList> call, Throwable t) {

                }
            });

        }
    }

    private void showList() {
        mCafes.addAll(mCafeList.getResults());
        mCafeListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (mIsFirstStart)
            loadCafeList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_favourite_list:
                if (mIsFavouriteList) {
                    mIsFavouriteList = false;
                    mImgFavourite.setImageDrawable(mNormal);
                    loadCafeList();
                } else {
                    mCafes.clear();
                    mIsFavouriteList = true;
                    mImgFavourite.setImageDrawable(mFavourite);
                    mCafeList.setResults(sDbHelper.getFavouriteCafeList(sProfile));
                    showList();
                }
                break;
        }
    }


}
