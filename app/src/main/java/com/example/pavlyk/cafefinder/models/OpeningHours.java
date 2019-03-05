
package com.example.pavlyk.cafefinder.models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class OpeningHours {

    @SerializedName("open_now")
    private Boolean openNow;
    @SerializedName("weekday_text")
    private List<Object> weekdayText = new ArrayList<Object>();

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public List<Object> getWeekdayText() {
        return weekdayText;
    }

    public void setWeekdayText(List<Object> weekdayText) {
        this.weekdayText = weekdayText;
    }

}
