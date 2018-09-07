package com.smarthouserental.network;

import com.smarthouserental.model.GoogleDirection;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RouteApi {

    @GET("/maps/api/directions/json")
    Call<GoogleDirection> getGoogleDirectionApi(@Query("origin")String origin, @Query("destination") String destination);
}
