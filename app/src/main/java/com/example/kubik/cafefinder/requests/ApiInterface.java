package com.example.kubik.cafefinder.requests;

import com.example.kubik.cafefinder.models.CafeInfo;
import com.example.kubik.cafefinder.models.CafeList;
import com.example.kubik.cafefinder.models.CafeReviewList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Kubik on 10/29/16.
 */

public interface ApiInterface {
    @GET("nearbysearch/json")
    Call<CafeList> getNearbyPlaces(@Query("location") String location,
                                   @Query("radius") double radius,
                                   @Query("rankby") String rankby,
                                   @Query("types") String type,
                                   @Query("key") String apiKey);

    @GET("nearbysearch/json")
    Call<CafeList> getNextPagePlaces(@Query("location") String location,
                                     @Query("radius") double radius,
                                     @Query("pagetoken") String pageToken,
                                     @Query("key") String apiKey);
    @GET("details/json")
    Call<CafeInfo> getCafeReviews(@Query("placeid") String placeId,
                                  @Query("key") String apiKey);
}
