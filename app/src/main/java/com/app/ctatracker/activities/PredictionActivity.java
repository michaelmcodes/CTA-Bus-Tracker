package com.app.ctatracker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.Constants;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.adapters.PredictionsViewAdapter;
import com.app.ctatracker.models.Prediction;
import com.app.ctatracker.models.PredictionsResponse;
import com.app.ctatracker.rest.ApiClient;
import com.app.ctatracker.rest.ApiInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PredictionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView textViewDirection, textViewTitle;
    private static final String TAG = PredictionActivity.class.getSimpleName();
    private String routeColor;
    private String routeName;
    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction);

        Objects.requireNonNull(getSupportActionBar()).hide();

        String routeNumber = getIntent().getStringExtra(Constants.ROUTE_NUMBER);
        routeColor = getIntent().getStringExtra(Constants.ROUTE_COLOR);
        String stopID = getIntent().getStringExtra(Constants.STOP_ID);
        routeName = getIntent().getStringExtra(Constants.ROUTE_NAME);
        String direction = getIntent().getStringExtra(Constants.DIRECTION);
        latitude = getIntent().getDoubleExtra(Constants.LATITUDE, 0.0);
        longitude = getIntent().getDoubleExtra(Constants.LONGITUDE, 0.0);


        recyclerView = findViewById(R.id.recycler_view);
        textViewDirection = findViewById(R.id.text_view_direction_name);
        textViewTitle = findViewById(R.id.text_view_title);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        textViewTitle.setText(getString(R.string.route) + " " + routeNumber + " - " + routeName);

        textViewDirection.setBackgroundColor(Color.parseColor(routeColor));
        textViewDirection.setText(routeName + " (" + direction + ")" + "\n" + Utils.getCurrentTime());
        textViewDirection.setTextColor(Utils.getTextColorForBackground(routeColor));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchPredictions(routeNumber, stopID);

        });

        fetchPredictions(routeNumber, stopID);

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }


    private void fetchPredictions(String route, String stopId) {
        ApiInterface apiService = ApiClient.getClient(false,this).create(ApiInterface.class);
        Call<PredictionsResponse> call = apiService.getPredictions(getString(R.string.API_KEY), Constants.FORMAT, route, stopId);

        call.enqueue(new Callback<PredictionsResponse>() {
            @Override
            public void onResponse(@NonNull Call<PredictionsResponse> call, @NonNull Response<PredictionsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Prediction> predictions = response.body().getBusTimeResponse().getPrd();

                    if (predictions == null || predictions.isEmpty()) {
                        Toast.makeText(PredictionActivity.this, getString(R.string.no_pred_available), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, getString(R.string.no_pred_available));
                        finish();
                    } else {
                        PredictionsViewAdapter adapter = new PredictionsViewAdapter(predictions, routeColor, routeName, latitude, longitude);
                        recyclerView.setAdapter(adapter);
                    }

                } else {
                    Toast.makeText(PredictionActivity.this, getString(R.string.error_fecthing_prediction), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, getString(R.string.error_fecthing_prediction));
                    finish();
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<PredictionsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, getString(R.string.error_fecthing_prediction) + t.getMessage());
            }
        });
    }
}