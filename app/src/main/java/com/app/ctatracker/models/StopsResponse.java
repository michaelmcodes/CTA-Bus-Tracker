package com.app.ctatracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StopsResponse {
    @SerializedName("bustime-response")
    private BusTimeResponse busTimeResponse;

    public BusTimeResponse getBusTimeResponse() {
        return busTimeResponse;
    }

    public static class BusTimeResponse {
        private List<Stop> stops;

        public List<Stop> getStops() {
            return stops;
        }
    }
}
