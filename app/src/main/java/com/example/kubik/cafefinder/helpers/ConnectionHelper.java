package com.example.kubik.cafefinder.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Class for detecting internet connection state.
 * It tries to connect 4 times with period 0.5 sec.
 * If it can`t connect in this period return false.
 */

public final class ConnectionHelper {

    private static final int DEFAULT_RETRY_COUNT = 4;
    private static final int DEFAULT_RETRY_PENDING = 500;

    private ConnectionHelper(){}

    public static boolean isConnected(Context context) throws InterruptedException {
        for (int i = 0; i < DEFAULT_RETRY_COUNT; i++) {
            if (isNetworkConnected(context)) {
                return true;
            } else {
                Thread.sleep(DEFAULT_RETRY_PENDING);
            }
        }
        return false;
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService
                (Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
