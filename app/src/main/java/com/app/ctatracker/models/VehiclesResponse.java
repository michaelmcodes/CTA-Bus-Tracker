package com.app.ctatracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VehiclesResponse {
    @SerializedName("bustime-response")
    private BusTimeResponse busTimeResponse;

    public BusTimeResponse getBusTimeResponse() {
        return busTimeResponse;
    }

    public static class BusTimeResponse {
        private List<Vehicle> vehicle;

        public List<Vehicle> getVehicle() {
            return vehicle;
        }
    }
}
