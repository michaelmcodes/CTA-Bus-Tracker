package com.app.ctatracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.Constants;
import com.app.ctatracker.Utils.LocationUtils;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.activities.PredictionActivity;
import com.app.ctatracker.models.Stop;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StopsViewAdapter extends RecyclerView.Adapter<StopsViewAdapter.ViewHolder> {
    private final List<Stop> stops;
    private final String routeNumber;
    private final String routeName;
    private final String routeColor;
    private final String routeDirection;
    private final double latitude;
    private final double longitude;
    private Context context;

    public StopsViewAdapter(List<Stop> stops, String routeColor, String routeName, String routeNumber, String routeDirection, double latitude, double longitude) {
        this.stops = new ArrayList<>(stops);
        this.routeColor = routeColor;
        this.routeNumber = routeNumber;
        this.routeName = routeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeDirection = routeDirection;

    }

    @NonNull
    @Override
    public StopsViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_item_stop, parent, false);
        context = parent.getContext();
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull StopsViewAdapter.ViewHolder holder, int position) {
        holder.textviewStopName.setText(stops.get(position).getStpnm());
        holder.textviewStopName.setTextColor(Utils.getTextColorForBackground(routeColor));
        holder.textviewStopDirection.setTextColor(Utils.getTextColorForBackground(routeColor));
        holder.linearLayoutStop.setBackgroundColor(Color.parseColor(routeColor));

        holder.linearLayoutStop.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PredictionActivity.class);
            intent.putExtra(Constants.STOP_ID, stops.get(position).getStpid());
            intent.putExtra(Constants.ROUTE_COLOR, routeColor);
            intent.putExtra(Constants.ROUTE_NAME, routeName);
            intent.putExtra(Constants.ROUTE_NUMBER, routeNumber);
            intent.putExtra(Constants.DIRECTION, routeDirection);
            intent.putExtra(Constants.LATITUDE, stops.get(position).getLat());
            intent.putExtra(Constants.LONGITUDE, stops.get(position).getLon());
            context.startActivity(intent);

        });

        if(latitude == 0.0 || longitude == 0.0) {
            holder.textviewStopDirection.setText("Location not available");
            return;
        }
        double distance = LocationUtils.calculateDistance(latitude, longitude, stops.get(position).getLat(), stops.get(position).getLon());
        double bearing = LocationUtils.calculateBearing(latitude, longitude, stops.get(position).getLat(), stops.get(position).getLon());
        String direction = LocationUtils.bearingToDirection(bearing);
        holder.textviewStopDirection.setText(String.format(Locale.getDefault(), "%.0f m %s of your location", distance, direction));

    }

    @Override
    public int getItemCount() {
        return stops.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewStopName;
        public TextView textviewStopDirection;
        public LinearLayout linearLayoutStop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textviewStopName = itemView.findViewById(R.id.textview_stop_name);
            this.textviewStopDirection = itemView.findViewById(R.id.textview_stop_direction);
            this.linearLayoutStop = itemView.findViewById(R.id.linear_layout_stop);
        }
    }


}
