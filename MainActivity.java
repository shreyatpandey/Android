package com.example.wifirtt;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private WifiRttManager wifiRttManager;
    private TextView tvResults;
    private Button btnStartRanging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResults = findViewById(R.id.tvResults);
        btnStartRanging = findViewById(R.id.btnStartRanging);

        // Initialize WifiRttManager
        wifiRttManager = (WifiRttManager) getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        // Request permissions
        if (checkPermissions()) {
            setupRanging();
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupRanging();
            } else {
                Toast.makeText(this, "Location permission is required for Wi-Fi RTT.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupRanging() {
        btnStartRanging.setOnClickListener(v -> startRanging());
    }

    private void startRanging() {
        RangingRequest.Builder builder = new RangingRequest.Builder();
        RangingRequest request = builder.build();

        if (wifiRttManager != null) {
            wifiRttManager.startRanging(request, getMainExecutor(), new RangingResultCallback() {
                @Override
                public void onRangingResults(@NonNull List<RangingResult> results) {
                    StringBuilder resultText = new StringBuilder();
                    for (RangingResult result : results) {
                        resultText.append("Distance: ").append(result.getDistanceMm()).append(" mm\n");
                        resultText.append("Status: ").append(result.getStatus()).append("\n\n");
                    }
                    tvResults.setText(resultText.toString());
                }

                @Override
                public void onRangingFailure(int code) {
                    tvResults.setText("Ranging failed with code: " + code);
                }
            });
        } else {
            Toast.makeText(this, "Wi-Fi RTT is not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }
}