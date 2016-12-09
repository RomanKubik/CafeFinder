package com.example.kubik.cafefinder.database.models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Kubik on 12/8/16.
 */

public class CafeInfo extends RealmObject {
    private String name;
    private String address;
    private String phoneNumber;
    private String link;
    private float rating;
    private double lat;
    private double lng;
    private RealmList<Image> images;
    private RealmList<Review> reviews;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public RealmList<Image> getImages() {
        return images;
    }

    public void setImages(RealmList<Image> images) {
        this.images = images;
    }

    public RealmList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(RealmList<Review> reviews) {
        this.reviews = reviews;
    }
}
