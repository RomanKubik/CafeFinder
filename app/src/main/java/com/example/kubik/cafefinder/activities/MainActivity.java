package com.example.kubik.cafefinder.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.adapters.DistanceSpinnerAdapter;
import com.example.kubik.cafefinder.adapters.MainCafeListAdapter;
import com.example.kubik.cafefinder.helpers.ApiUrlBuilder;
import com.example.kubik.cafefinder.models.BaseCafeInfo;
import com.example.kubik.cafefinder.models.CafeList;
import com.example.kubik.cafefinder.requests.ApiClient;
import com.example.kubik.cafefinder.requests.ApiInterface;

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
public class MainActivity extends BaseCafeActivity {

    public static final String TAG = "MainActivity";

    public static final String EXTRA_PLACE_ID = "EXTRA_PLACE_ID";

    private int mSearchRadius = 250;

    private CafeList mCafeList;

    @BindView(R.id.rv_main_cafe_list)
    RecyclerView mRecyclerView;

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
    }

       @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
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

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        String location = sLocation.getLatitude() + "," + sLocation.getLongitude();
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
        MainCafeListAdapter mCafeListAdapter = new MainCafeListAdapter(cafes, this, sLocation);
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
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        if (mIsFirstStart)
            loadCafeList();
    }

}
