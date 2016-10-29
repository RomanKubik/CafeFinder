package com.example.kubik.cafefinder.helpers;

/**
 * Builder for both url which will be use app
 */

public final class ApiUrlBuilder {

    private static final String API_KEY = "AIzaSyD4XuS58KfQFN_F9DxWYv3hPIu_IULpwl4";

    private static final String PHOTO_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo?";
    private static final int DEFAULT_PHOTO_HEIGHT = 500;

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getPhotoUrl(int maxHeight, String photoReference) {
        return PHOTO_BASE_URL + "maxheight=" + String.valueOf(maxHeight) + "&photoreference="
                + photoReference + API_KEY;
    }
    public static String getPhotoUrl(String photoReference) {
        return PHOTO_BASE_URL + "maxheight=" + String.valueOf(DEFAULT_PHOTO_HEIGHT)
                + "&photoreference=" + photoReference + "&key=" + API_KEY;
    }
}
