package com.smarthouserental.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Routes {

    @SerializedName("legs")
    private List<Legs> legs;

    public List<Legs> getLegs() {
        return legs;
    }
}
