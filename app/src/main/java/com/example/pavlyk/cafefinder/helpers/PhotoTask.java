package com.example.pavlyk.cafefinder.helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kubik on 11/15/16.
 */

public class PhotoTask extends AsyncTask<String, Void, PhotoTask.AttributedPhoto> {

    private int mHeight;
    private int mWidth;
    private GoogleApiClient mGoogleApiClient;

    public PhotoTask(int width, int height, GoogleApiClient googleApiClient) {
        mHeight = height;
        mWidth = width;
        mGoogleApiClient = googleApiClient;
    }

    /**
     * Loads the first photo for a place id from the Geo Data API.
     * The place id must be the first (and only) parameter.
     */
    @Override
    protected AttributedPhoto doInBackground(String... strings) {
        if (strings.length != 1) {
            return null;
        }
        final String placeId = strings[0];
        AttributedPhoto attributedPhoto = new AttributedPhoto();

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get all bitmaps
                for ( int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                    PlacePhotoMetadata photo = photoMetadataBuffer.get(i);
                    // Load a scaled bitmap for this photo.
                    Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                            .getBitmap();

                    attributedPhoto.addAttributedPhoto(image);
                }

            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }
        return attributedPhoto;
    }

    /**
     * Holder for an image and its attribution.
     */
    protected class AttributedPhoto {
        private List<Bitmap> bitmaps = new ArrayList<>();


        void addAttributedPhoto(@NonNull Bitmap bitmap) {
            bitmaps.add(bitmap);
        }

        @Nullable
        public Bitmap getBitmap(@NonNull int i) {
            if (i >= 0 & i < bitmaps.size()) {
                return bitmaps.get(i);
            } else {
                return null;
            }
        }

        @NonNull
        public List<Bitmap> getBitmapList() {
            return bitmaps;
        }
    }
}
