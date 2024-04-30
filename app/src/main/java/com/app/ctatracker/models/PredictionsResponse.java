package com.app.ctatracker.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PredictionsResponse {
    @SerializedName("bustime-response")
    private BusTimeResponse busTimeResponse;

    public BusTimeResponse getBusTimeResponse() {
        return busTimeResponse;
    }

    public static class BusTimeResponse {
        private List<Prediction> prd;

        public List<Prediction> getPrd() {
            return prd;
        }
    }
}
