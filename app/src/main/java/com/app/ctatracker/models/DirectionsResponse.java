package com.app.ctatracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DirectionsResponse {
    @SerializedName("bustime-response")
    private BusTimeResponse busTimeResponse;

    public BusTimeResponse getBusTimeResponse() {
        return busTimeResponse;
    }

    public static class BusTimeResponse {
        private List<Direction> directions;

        public List<Direction> getDirections() {
            return directions;
        }
    }
}
