package com.example.wifiaware;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.aware.AttachCallback;
import android.net.wifi.aware.DiscoverySessionCallback;
import android.net.wifi.aware.PeerHandle;
import android.net.wifi.aware.PublishConfig;
import android.net.wifi.aware.PublishDiscoverySession;
import android.net.wifi.aware.SubscribeConfig;
import android.net.wifi.aware.SubscribeDiscoverySession;
import android.net.wifi.aware.WifiAwareManager;
import android.net.wifi.aware.WifiAwareSession;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_CODE = 1;
    private WifiAwareManager wifiAwareManager;
    private WifiAwareSession wifiAwareSession;
    private PeerHandle peerHandle;
    private TextView tvResults;
    private Button btnStartDiscovery, btnSendMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResults = findViewById(R.id.tvResults);
        btnStartDiscovery = findViewById(R.id.btnStartDiscovery);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        // Initialize WifiAwareManager
        wifiAwareManager = (WifiAwareManager) getSystemService(Context.WIFI_AWARE_SERVICE);

        // Request permissions
        if (checkPermissions()) {
            setupWiFiAware();
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
                setupWiFiAware();
            } else {
                Toast.makeText(this, "Location permission is required for Wi-Fi Aware.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupWiFiAware() {
        btnStartDiscovery.setOnClickListener(v -> startDiscovery());
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void startDiscovery() {
        if (wifiAwareManager == null) {
            Toast.makeText(this, "Wi-Fi Aware is not supported on this device.", Toast.LENGTH_SHORT).show();
            return;
        }

        wifiAwareManager.attach(new AttachCallback() {
            @Override
            public void onAttached(WifiAwareSession session) {
                wifiAwareSession = session;
                startPublish();
                startSubscribe();
            }

            @Override
            public void onAttachFailed() {
                tvResults.setText("Wi-Fi Aware attach failed.");
            }
        }, null);
    }

    private void startPublish() {
        PublishConfig publishConfig = new PublishConfig.Builder()
                .setServiceName("WiFiAwareDemo")
                .build();

        wifiAwareSession.publish(publishConfig, new DiscoverySessionCallback() {
            @Override
            public void onPublishStarted(@NonNull PublishDiscoverySession session) {
                tvResults.setText("Publish started.");
            }

            @Override
            public void onMessageReceived(PeerHandle peerHandle, byte[] message) {
                String receivedMessage = new String(message);
                tvResults.setText("Received: " + receivedMessage);
            }
        }, null);
    }

    private void startSubscribe() {
        SubscribeConfig subscribeConfig = new SubscribeConfig.Builder()
                .setServiceName("WiFiAwareDemo")
                .build();

        wifiAwareSession.subscribe(subscribeConfig, new DiscoverySessionCallback() {
            @Override
            public void onSubscribeStarted(@NonNull SubscribeDiscoverySession session) {
                tvResults.setText("Subscribe started.");
            }

            @Override
            public void onMessageReceived(PeerHandle peerHandle, byte[] message) {
                String receivedMessage = new String(message);
                tvResults.setText("Received: " + receivedMessage);
            }
        }, null);
    }

    private void sendMessage() {
        if (peerHandle == null) {
            Toast.makeText(this, "No peer discovered yet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String message = "Hello from Wi-Fi Aware!";
        wifiAwareSession.sendMessage(peerHandle, 1, message.getBytes());
        tvResults.setText("Sent: " + message);
    }
}