package com.example.pavlyk.cafefinder.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Model to contain list of cafe`s.
 */

public class CafeList {
    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("results")
    private List<BaseCafeInfo> results = new ArrayList<>();
    @SerializedName("status")
    private String status;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<BaseCafeInfo> getResults() {
        return results;
    }

    public void setResults(List<BaseCafeInfo> results) {
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
