package com.example.kubik.cafefinder.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.kubik.cafefinder.R;
import com.example.kubik.cafefinder.helpers.ConnectionHelper;

import butterknife.ButterKnife;

/**
 * Created by Kubik on 10/22/16.
 */

public class BaseCafeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        try {
            if (!ConnectionHelper.isConnected(this)) {
                Toast.makeText(this, R.string.not_connected_msg, Toast.LENGTH_LONG).show();
            }
        } catch (InterruptedException e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }
}
