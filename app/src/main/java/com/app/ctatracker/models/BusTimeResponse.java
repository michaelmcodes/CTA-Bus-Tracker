package com.app.ctatracker.models;

import com.google.gson.annotations.SerializedName;

public class BusTimeResponse {
    @SerializedName("bustime-response")
    private RoutesResponse bustimeResponse;

    public RoutesResponse getBustimeResponse() {
        return bustimeResponse;
    }
}

