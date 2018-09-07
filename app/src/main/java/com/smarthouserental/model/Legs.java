package com.smarthouserental.model;

import com.google.gson.annotations.SerializedName;

public class Legs {

    @SerializedName("duration")
    private Duration duration;

    @SerializedName("distance")
    private Distance distance;


    public Duration getDuration() {
        return duration;
    }

    public Distance getDistance() {
        return distance;
    }
}
