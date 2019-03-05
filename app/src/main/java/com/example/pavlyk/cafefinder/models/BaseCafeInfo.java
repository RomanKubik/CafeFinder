package com.example.pavlyk.cafefinder.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for storage base information of cafe which will be shown on main cafe list.
 */

public class BaseCafeInfo {
    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("name")
    private String name;
    @SerializedName("opening_hours")
    private OpeningHours openingHours;
    @SerializedName("photos")
    private List<Photo> photos = new ArrayList<>();
    @SerializedName("place_id")
    private String placeId;
    @SerializedName("rating")
    private double rating;
    @SerializedName("types")
    private List<String> types = new ArrayList<>();
    @SerializedName("vicinity")
    private String vicinity;
    //For cached poster
    private Bitmap poster;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public Bitmap getPoster() {
        return poster;
    }

    public void setPoster(Bitmap poster) {
        this.poster = poster;
    }
}
