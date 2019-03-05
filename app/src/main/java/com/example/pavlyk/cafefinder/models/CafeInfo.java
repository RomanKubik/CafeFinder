package com.example.pavlyk.cafefinder.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Kubik on 12/8/16.
 */

public class CafeInfo {
    @SerializedName("result")
    private CafeReviewList result;
    @SerializedName("status")
    private String status;

    public CafeReviewList getResult() {
        return result;
    }

    public void setResult(CafeReviewList result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
