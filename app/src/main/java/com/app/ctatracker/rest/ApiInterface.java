package com.app.ctatracker.rest;

import com.app.ctatracker.models.BusTimeResponse;
import com.app.ctatracker.models.DirectionsResponse;
import com.app.ctatracker.models.PredictionsResponse;
import com.app.ctatracker.models.StopsResponse;
import com.app.ctatracker.models.VehiclesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("bustime/api/v2/getroutes")
    Call<BusTimeResponse> getRoutes(@Query("key") String apiKey, @Query("format") String format);

    @GET("bustime/api/v2/getdirections")
    Call<DirectionsResponse> getDirections(@Query("key") String apiKey, @Query("format") String format, @Query("rt") String rt);

    @GET("bustime/api/v2/getstops")
    Call<StopsResponse> getStops(
            @Query("key") String apiKey,
            @Query("format") String format,
            @Query("rt") String rt,
            @Query("dir") String dir);

    @GET("bustime/api/v2/getpredictions")
    Call<PredictionsResponse> getPredictions(
            @Query("key") String apiKey,
            @Query("format") String format,
            @Query("rt") String rt,
            @Query("stpid") String stpid);

    @GET("bustime/api/v2/getvehicles")
    Call<VehiclesResponse> getVehicles(
            @Query("key") String apiKey,
            @Query("format") String format,
            @Query("vid") String vid);


}
