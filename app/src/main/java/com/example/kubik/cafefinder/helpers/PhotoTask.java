package com.example.kubik.cafefinder.helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
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
        AttributedPhoto attributedPhoto = null;

        PlacePhotoMetadataResult result = Places.GeoDataApi
                .getPlacePhotos(mGoogleApiClient, placeId).await();

        if (result.getStatus().isSuccess()) {
            PlacePhotoMetadataBuffer photoMetadataBuffer = result.getPhotoMetadata();
            if (photoMetadataBuffer.getCount() > 0 && !isCancelled()) {
                // Get the first bitmap and its attributions.
                PlacePhotoMetadata photo = photoMetadataBuffer.get(0);
                CharSequence attribution = photo.getAttributions();
                // Load a scaled bitmap for this photo.
                Bitmap image = photo.getScaledPhoto(mGoogleApiClient, mWidth, mHeight).await()
                        .getBitmap();

                attributedPhoto.addAttributedPhoto(attribution, image);
            }
            // Release the PlacePhotoMetadataBuffer.
            photoMetadataBuffer.release();
        }
        return attributedPhoto;
    }

    /**
     * Holder for an image and its attribution.
     */
    class AttributedPhoto {
        List<CharSequence> attributions = new ArrayList<>();
        List<Bitmap> bitmaps = new ArrayList<>();


        void addAttributedPhoto(@NonNull CharSequence attribution, @NonNull Bitmap bitmap) {
            attributions.add(attribution);
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

        @Nullable
        public CharSequence getAttribution(@NonNull int i) {
            if (i >= 0 & i < attributions.size()) {
                return attributions.get(i);
            } else {
                return null;
            }
        }
    }
}
