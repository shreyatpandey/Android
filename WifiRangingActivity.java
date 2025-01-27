import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class WifiRangingActivity extends AppCompatActivity {

    private static final String TAG = "WifiRangingActivity";
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;

    private WifiManager wifiManager;
    private TextView wifiRangingResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_ranging);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiRangingResultTextView = findViewById(R.id.wifi_ranging_result);

        // Check for location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                startWifiScan();
            }
        } else {
            startWifiScan();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startWifiScan();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startWifiScan() {
        wifiManager.startScan();
        wifiManager.getScanResults(); 
    }

    // Implement a callback to handle the scan results
    private WifiManager.WifiScanCallback wifiScanCallback = new WifiManager.WifiScanCallback() {
        @Override
        public void onScanResultsAvailable(List<ScanResult> results) {
            StringBuilder sb = new StringBuilder();
            sb.append("Wifi Scan Results:\n");
            for (ScanResult result : results) {
                sb.append("BSSID: ").append(result.BSSID).append("\n");
                sb.append("SSID: ").append(result.SSID).append("\n");
                sb.append("Level: ").append(result.level).append(" dBm\n");
                sb.append("\n");
            }
            wifiRangingResultTextView.setText(sb.toString());
        }
    };
}