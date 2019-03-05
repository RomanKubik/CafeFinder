package com.example.pavlyk.cafefinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kubik on 12/8/16.
 */

public class CafeReviewList {
    @SerializedName("reviews")
    private List<CafeReview> reviews = new ArrayList<>();

    public List<CafeReview> getReviews() {
        return reviews;
    }

    public void setReviews(List<CafeReview> reviews) {
        this.reviews = reviews;
    }
}
