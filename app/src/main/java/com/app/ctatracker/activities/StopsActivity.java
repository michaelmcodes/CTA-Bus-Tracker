package com.app.ctatracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.Constants;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.adapters.StopsViewAdapter;
import com.app.ctatracker.models.Stop;
import com.app.ctatracker.models.StopsResponse;
import com.app.ctatracker.rest.ApiClient;
import com.app.ctatracker.rest.ApiInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StopsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    private static final String TAG = StopsActivity.class.getSimpleName();
    private String routeColor;
    private FusedLocationProviderClient fusedLocationClient;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private String routeDirection;
    private String routeNumber;
    private String routeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stops);

        Objects.requireNonNull(getSupportActionBar()).hide();

        routeNumber = getIntent().getStringExtra(Constants.ROUTE_NUMBER);
        routeName = getIntent().getStringExtra(Constants.ROUTE_NAME);
        routeDirection = getIntent().getStringExtra(Constants.DIRECTION);
        routeColor = getIntent().getStringExtra(Constants.COLOR);


        TextView textViewTitle = findViewById(R.id.text_view_title);
        TextView textViewDirection = findViewById(R.id.text_view_direction_name);
        textViewTitle.setText(getString(R.string.route) + " " + routeNumber + " - " + routeName);
        textViewDirection.setText(routeDirection + " " + getString(R.string.stops));
        textViewDirection.setBackgroundColor(Color.parseColor(routeColor));
        textViewDirection.setTextColor(Utils.getTextColorForBackground(routeColor));
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getLocation();

        fetchStops(routeNumber, routeDirection);

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    private void fetchStops(String route, String direction) {
        ApiInterface apiService = ApiClient.getClient(true, this).create(ApiInterface.class);
        Call<StopsResponse> call = apiService.getStops(getString(R.string.API_KEY), "json", route, direction);

        call.enqueue(new Callback<StopsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StopsResponse> call, @NonNull Response<StopsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Stop> stops = response.body().getBusTimeResponse().getStops();
                    if (stops == null || stops.isEmpty()) {
                        Log.e(TAG, "No stops found");
                        Toast.makeText(StopsActivity.this, "No stops found", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        recyclerView.setAdapter(new StopsViewAdapter(stops, routeColor, routeName, routeNumber, routeDirection, currentLatitude, currentLongitude));
                    }

                } else {
                    Log.e(TAG, "Failed to fetch stops: " + response.message());
                }
            }

            @Override
            public void onFailure(@NonNull Call<StopsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Failed to fetch stops: " + t.getMessage());
            }
        });

    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLatitude = location.getLatitude();
                            currentLongitude = location.getLongitude();
                        }
                    });
        }

    }
}