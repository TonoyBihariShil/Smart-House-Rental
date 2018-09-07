package com.smarthouserental.model;

import com.google.gson.annotations.SerializedName;

public class Duration {

    @SerializedName("text")
    private String durationText;

    public String getDurationText() {
        return durationText;
    }
}
