package com.example.kubik.cafefinder.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.kubik.cafefinder.R;

/**
 * Activity for main window of app.
 */
public class MainActivity extends BaseCafeActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }
}
