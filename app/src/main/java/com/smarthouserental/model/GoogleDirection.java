package com.smarthouserental.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GoogleDirection {

    @SerializedName("routes")
    private List<Routes> routeList;

    public List<Routes> getRouteList() {
        return routeList;
    }
}
