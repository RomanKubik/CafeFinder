package com.example.kubik.cafefinder.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.kubik.cafefinder.R;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseCafeActivity {

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


    }

}
