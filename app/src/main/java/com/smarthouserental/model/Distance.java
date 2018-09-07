package com.smarthouserental.model;

import com.google.gson.annotations.SerializedName;

public class Distance {
    @SerializedName("text")
    private String distanceText;

    public String getDistanceText() {
        return distanceText;
    }
}
