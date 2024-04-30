package com.app.ctatracker.activities;

import static com.app.ctatracker.Utils.Utils.dialog;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.ctatracker.R;
import com.app.ctatracker.Utils.Constants;
import com.app.ctatracker.Utils.Utils;
import com.app.ctatracker.adapters.RoutesViewAdapter;
import com.app.ctatracker.models.BusTimeResponse;
import com.app.ctatracker.models.Route;
import com.app.ctatracker.rest.ApiClient;
import com.app.ctatracker.rest.ApiInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextInputEditText textInputEditText;
    private ImageView imageViewInfo;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private View content;
    private boolean isDataLoaded = false;
    private RoutesViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        textInputEditText = findViewById(R.id.text_input_edit_text);
        imageViewInfo = findViewById(R.id.image_view_info);

        Objects.requireNonNull(getSupportActionBar()).hide();

        content = findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        if (isDataLoaded) {
                            content.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        } else {
                            return false;
                        }

                    }
                });


        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null && s.length() > 0)
                    adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imageViewInfo.setOnClickListener(v -> {
            dialog(this, getString(R.string.bus_tracker_cta), getString(R.string.info), getString(R.string.ok), "", (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    dialog.dismiss();
                }
            });

        });


        requestLocationPermission();

        MobileAds.initialize(this, initializationStatus -> {
        });
        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void getRoutes() {
        ApiInterface apiClient = ApiClient.getClient(true, this).create(ApiInterface.class);
        Call<BusTimeResponse> call = apiClient.getRoutes(getString(R.string.API_KEY), Constants.FORMAT);
        call.enqueue(new Callback<BusTimeResponse>() {
            @Override
            public void onResponse(Call<BusTimeResponse> call, Response<BusTimeResponse> response) {
                if (response.isSuccessful()) {
                    BusTimeResponse busTimeResponse = response.body();
                    List<Route> routes = busTimeResponse.getBustimeResponse().getRoutes();
                    adapter = new RoutesViewAdapter(routes);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(adapter);
                    isDataLoaded = true;
                    content.requestLayout();

                }
            }

            @Override
            public void onFailure(Call<BusTimeResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Error fetching routes", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

            dialog(this, getString(R.string.fine_accuracy_needed), getString(R.string.loacation_request_rationale), getString(R.string.yes), getString(R.string.no_thanks), (dialog, which) -> {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    dialog.dismiss();
                    dialog(this, getString(R.string.fine_accuracy_needed), getString(R.string.loacation_request_rationale_denied), "", getString(R.string.no_thanks), (m_dialog, m_which) -> {
                        if (m_which == DialogInterface.BUTTON_NEGATIVE) {
                            finish();
                        }
                    });
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getRoutes();
            } else {
                Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                finish();
            }
        }
    }


}
