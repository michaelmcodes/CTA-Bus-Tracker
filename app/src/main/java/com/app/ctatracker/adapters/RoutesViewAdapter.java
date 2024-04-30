package com.app.ctatracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.Constants;
import com.app.ctatracker.activities.StopsActivity;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.models.Direction;
import com.app.ctatracker.models.DirectionsResponse;
import com.app.ctatracker.models.Route;
import com.app.ctatracker.rest.ApiClient;
import com.app.ctatracker.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutesViewAdapter extends RecyclerView.Adapter<RoutesViewAdapter.ViewHolder> implements RoutesViewAdapterFilter {
    private List<Route> originalData;
    private List<Route> filteredData;
    private Context context;
    private static final String TAG = RoutesViewAdapter.class.getSimpleName();

    public RoutesViewAdapter(List<Route> routes) {
        this.originalData = routes;
        this.filteredData = new ArrayList<>(routes);
    }

    @NonNull
    @Override
    public RoutesViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.layout_item_route, parent, false);
        context = parent.getContext();
        return new ViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(@NonNull RoutesViewAdapter.ViewHolder holder, int position) {
        holder.textviewRouteNumber.setText(filteredData.get(position).getRt());
        holder.textviewRouteName.setText(filteredData.get(position).getRtnm());
        holder.linearLayoutRoute.setBackgroundColor(Color.parseColor(filteredData.get(position).getRtclr()));
        holder.textviewRouteNumber.setTextColor(Utils.getTextColorForBackground(filteredData.get(position).getRtclr()));
        holder.textviewRouteName.setTextColor(Utils.getTextColorForBackground(filteredData.get(position).getRtclr()));

        holder.linearLayoutRoute.setOnClickListener(v -> {
            fetchDirections(filteredData.get(position).getRt(), filteredData.get(position).getRtnm(), filteredData.get(position).getRtclr(), v);
        });

    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Route> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalData);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Route item : originalData) {
                        if ((item.getRt() + item.getRtnm()).toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredData.clear();
                filteredData.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };
    }


    @Override
    public Route getItem(int position) {
        return filteredData.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textviewRouteNumber;
        public TextView textviewRouteName;
        public LinearLayout linearLayoutRoute;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textviewRouteNumber = itemView.findViewById(R.id.textview_route_number);
            textviewRouteName = itemView.findViewById(R.id.textview_route_name);
            linearLayoutRoute = itemView.findViewById(R.id.linear_layout_route);
        }
    }


    private void fetchDirections(String routeNumber, String routeName, String color, View v) {
        ApiInterface apiService = ApiClient.getClient(true, context).create(ApiInterface.class);
        Call<DirectionsResponse> call = apiService.getDirections("gLq3fq5BkExQt6PpXh9PrUSAX", "json", routeNumber);

        call.enqueue(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Direction> directions = response.body().getBusTimeResponse().getDirections();
                    PopupMenu popupMenu = new PopupMenu(context, v);
                    Menu menu = popupMenu.getMenu();

                    for (int i = 0; i < directions.size(); i++) {
                        menu.add(0, Menu.NONE, i, directions.get(i).getDir());

                    }

                    popupMenu.setOnMenuItemClickListener(item -> {
                        Intent intent = new Intent(context, StopsActivity.class);
                        intent.putExtra(Constants.ROUTE_NUMBER, routeNumber);
                        intent.putExtra(Constants.DIRECTION, directions.get(item.getOrder()).getDir());
                        intent.putExtra(Constants.ROUTE_NAME, routeName);
                        intent.putExtra(Constants.COLOR, color);
                        context.startActivity(intent);
                        return true;
                    });

                    popupMenu.show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch directions: " + t.getMessage());
            }
        });
    }

}
