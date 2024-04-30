package com.app.ctatracker.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.LocationUtils;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.models.Prediction;
import com.app.ctatracker.models.Vehicle;
import com.app.ctatracker.models.VehiclesResponse;
import com.app.ctatracker.rest.ApiClient;
import com.app.ctatracker.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionsViewAdapter extends RecyclerView.Adapter<PredictionsViewAdapter.ViewHolder> {
    private final List<Prediction> predictions;
    private final String routeColor;
    private final String routeName;
    private final double latitude;
    private final double longitude;
    private Context context;
    private static final String TAG = PredictionsViewAdapter.class.getSimpleName();

    public PredictionsViewAdapter(List<Prediction> predictions, String routeColor, String routeName, double latitude, double longitude) {
        this.predictions = new ArrayList<>(predictions);
        this.routeColor = routeColor;
        this.routeName = routeName;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    @NonNull
    @Override
    public PredictionsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_item_prediction, parent, false);
        context = parent.getContext();
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionsViewAdapter.ViewHolder holder, int position) {

        holder.textViewBusNumber.setText(predictions.get(position).getVid());
        holder.textViewBusNumber.setTextColor(Utils.getTextColorForBackground(routeColor));
        holder.textviewStopDirection.setText(predictions.get(position).getRtdir() + "to " + predictions.get(position).getDes());
        holder.textviewStopDirection.setTextColor(Utils.getTextColorForBackground(routeColor));
        holder.textViewDue.setText("Due in " + predictions.get(position).getPrdctdn() + " mins at");
        holder.textViewDue.setTextColor(Utils.getTextColorForBackground(routeColor));
        holder.textviewTime.setText(Utils.convertTimeFormat(predictions.get(position).getPrdtm()));
        holder.textviewTime.setTextColor(Utils.getTextColorForBackground(routeColor));

        holder.linearLayoutPrediction.setBackgroundColor(Color.parseColor(routeColor));


        holder.linearLayoutPrediction.setOnClickListener(v -> {
            fetchVehicleInfo(predictions.get(position).getVid(), position);
        });

    }

    @Override
    public int getItemCount() {
        return predictions.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewBusNumber;
        public TextView textViewDue;
        public TextView textviewTime;
        public TextView textviewStopDirection;
        public LinearLayout linearLayoutPrediction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewBusNumber = itemView.findViewById(R.id.textview_bus_number);
            this.textViewDue = itemView.findViewById(R.id.textview_due);
            this.textviewTime = itemView.findViewById(R.id.textview_time);
            this.textviewStopDirection = itemView.findViewById(R.id.textview_stop_direction);
            this.linearLayoutPrediction = itemView.findViewById(R.id.linear_layout_prediction);
        }
    }

    private void fetchVehicleInfo(String vehicleId, int position) {
        ApiInterface apiService = ApiClient.getClient(false, context).create(ApiInterface.class);
        Call<VehiclesResponse> call = apiService.getVehicles(context.getString(R.string.API_KEY), "json", vehicleId);

        call.enqueue(new Callback<VehiclesResponse>() {
            @Override
            public void onResponse(Call<VehiclesResponse> call, Response<VehiclesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Vehicle> vehicles = response.body().getBusTimeResponse().getVehicle();
                    if (vehicles == null || vehicles.isEmpty()) {
                        Log.e(TAG, "No vehicle information found for vehicleId: " + vehicleId);
                    } else {
                        double vehicleLatitude = Double.parseDouble(vehicles.get(0).getLat());
                        double vehicleLongitude = Double.parseDouble(vehicles.get(0).getLon());

                        double distance = 0;
                        if (vehicleLatitude != 0 && vehicleLongitude != 0) {
                            distance = LocationUtils.calculateDistance(latitude, longitude, vehicleLatitude, vehicleLongitude);
                        }

                        String message = String.format("Bus  %s is %s miles (%s min) away from the %s stop", predictions.get(position).getVid(), Math.round(distance * 10) / 10.0, predictions.get(position).getPrdctdn(), routeName);

                        Utils.dialog(context, context.getString(R.string.bus_number) + predictions.get(position).getVid(),
                                message, context.getString(R.string.ok), "", (dialogInterface, i) -> dialogInterface.dismiss());
                    }

                } else {
                    Log.e(TAG, "Failed to fetch vehicle information: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<VehiclesResponse> call, Throwable t) {
                Log.e(TAG, "Failed to fetch vehicle information: " + t.getMessage());
            }
        });
    }


}
