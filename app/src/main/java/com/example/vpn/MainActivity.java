package com.example.vpn;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * MainActivity - Demonstrates how to request phone permissions in a VPN app
 * 
 * This activity handles:
 * 1. Runtime permission requests (READ_PHONE_STATE)
 * 2. VPN service permission request
 * 3. User feedback and permission status display
 */
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PHONE_STATE = 100;
    
    private Button btnRequestPermissions;
    private Button btnStartVpn;
    private TextView tvPermissionStatus;
    
    // Modern ActivityResultLauncher for VPN permission
    private ActivityResultLauncher<Intent> vpnPermissionLauncher;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Register ActivityResultLauncher for VPN permission
        vpnPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    handleVpnPermissionResult(result.getResultCode());
                }
            }
        );
        
        // Initialize UI components
        btnRequestPermissions = findViewById(R.id.btn_request_permissions);
        btnStartVpn = findViewById(R.id.btn_start_vpn);
        tvPermissionStatus = findViewById(R.id.tv_permission_status);
        
        // Set click listeners
        btnRequestPermissions.setOnClickListener(v -> requestPhonePermission());
        btnStartVpn.setOnClickListener(v -> requestVpnPermission());
        
        // Update permission status on startup
        updatePermissionStatus();
    }
    
    /**
     * Request READ_PHONE_STATE permission from the user
     * This demonstrates the standard Android runtime permission request flow
     */
    private void requestPhonePermission() {
        // Check if we already have the permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Phone permission already granted!", Toast.LENGTH_SHORT).show();
            updatePermissionStatus();
            return;
        }
        
        // Check if we should show rationale
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, 
                Manifest.permission.READ_PHONE_STATE)) {
            // Show explanation to the user
            new AlertDialog.Builder(this)
                .setTitle("Phone Permission Required")
                .setMessage("This VPN app needs access to phone state to detect network changes " +
                           "and maintain stable VPN connections. Your privacy is important to us " +
                           "and this information is only used for VPN functionality.")
                .setPositiveButton("Grant Permission", (dialog, which) -> {
                    // Request the permission after showing rationale
                    ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        REQUEST_CODE_PHONE_STATE);
                })
                .setNegativeButton("Cancel", null)
                .show();
        } else {
            // No explanation needed, directly request the permission
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                REQUEST_CODE_PHONE_STATE);
        }
    }
    
    /**
     * Request VPN service permission from the user
     * This is required before the app can establish a VPN connection
     */
    private void requestVpnPermission() {
        // First check if phone permission is granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant phone permission first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Prepare VPN service
        Intent intent = VpnService.prepare(this);
        if (intent != null) {
            // VPN permission not granted, request it using modern API
            vpnPermissionLauncher.launch(intent);
        } else {
            // VPN permission already granted
            Toast.makeText(this, "VPN permission already granted! Ready to connect.", 
                          Toast.LENGTH_SHORT).show();
            updatePermissionStatus();
        }
    }
    
    /**
     * Handle the result of permission requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_PHONE_STATE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Phone permission granted!", Toast.LENGTH_SHORT).show();
                updatePermissionStatus();
            } else {
                // Permission denied
                Toast.makeText(this, "Phone permission denied. Some features may not work.", 
                              Toast.LENGTH_LONG).show();
                
                // Check if user selected "Don't ask again"
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, 
                        Manifest.permission.READ_PHONE_STATE)) {
                    showPermissionDeniedDialog();
                }
            }
        }
    }
    
    /**
     * Handle the result of VPN permission request
     */
    private void handleVpnPermissionResult(int resultCode) {
        if (resultCode == Activity.RESULT_OK) {
            // VPN permission granted
            Toast.makeText(this, "VPN permission granted! Ready to connect.", 
                          Toast.LENGTH_SHORT).show();
            updatePermissionStatus();
        } else {
            // VPN permission denied
            Toast.makeText(this, "VPN permission denied. Cannot establish VPN connection.", 
                          Toast.LENGTH_LONG).show();
        }
    }
    
    /**
     * Update the permission status display
     */
    private void updatePermissionStatus() {
        StringBuilder status = new StringBuilder("Permission Status:\n\n");
        
        // Check phone state permission
        boolean hasPhonePermission = ContextCompat.checkSelfPermission(this, 
            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        status.append("📱 READ_PHONE_STATE: ")
              .append(hasPhonePermission ? "✅ Granted" : "❌ Not Granted")
              .append("\n\n");
        
        // Check VPN permission
        Intent vpnIntent = VpnService.prepare(this);
        boolean hasVpnPermission = (vpnIntent == null);
        status.append("🔒 VPN Service: ")
              .append(hasVpnPermission ? "✅ Granted" : "❌ Not Granted")
              .append("\n\n");
        
        // Add information about other permissions
        status.append("ℹ️ Other Permissions:\n");
        status.append("• INTERNET: Granted (Install-time)\n");
        status.append("• ACCESS_NETWORK_STATE: Granted (Install-time)\n");
        status.append("• CHANGE_NETWORK_STATE: Granted (Install-time)");
        
        tvPermissionStatus.setText(status.toString());
    }
    
    /**
     * Show dialog when permission is permanently denied
     */
    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Phone permission is required for proper VPN functionality. " +
                       "You have denied this permission. Please go to Settings > Apps > " +
                       "VPN > Permissions to enable it manually.")
            .setPositiveButton("OK", null)
            .show();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update status when returning to the activity
        updatePermissionStatus();
    }
}
