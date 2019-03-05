package com.example.pavlyk.cafefinder.database.models;

import io.realm.RealmObject;

/**
 * Created by Kubik on 12/8/16.
 */

public class Review extends RealmObject {
    private String reviewerName;
    private String review;
    private double rating;

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
