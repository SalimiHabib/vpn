# Quick Reference: Phone Permission in VPN App

## TL;DR - How Phone Permissions Work in This VPN App

### What Permissions Are Needed?

1. **READ_PHONE_STATE** (Runtime) - Requires user approval
2. **VPN Service** (Special) - Requires system authorization
3. **Network Permissions** (Install-time) - Auto-granted

### How to Request Them?

**In MainActivity.java:**

```java
// Request phone permission
private void requestPhonePermission() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) 
            != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_PHONE_STATE},
            REQUEST_CODE_PHONE_STATE);
    }
}

// Request VPN permission
private void requestVpnPermission() {
    Intent intent = VpnService.prepare(this);
    if (intent != null) {
        startActivityForResult(intent, REQUEST_CODE_VPN);
    }
}
```

### How to Handle Results?

```java
@Override
public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    if (requestCode == REQUEST_CODE_PHONE_STATE) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Phone permission granted!
        } else {
            // Phone permission denied
        }
    }
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_CODE_VPN) {
        if (resultCode == RESULT_OK) {
            // VPN permission granted!
        } else {
            // VPN permission denied
        }
    }
}
```

## File Locations

```
Key Implementation Files:
├── AndroidManifest.xml              - Permission declarations
├── MainActivity.java                - Permission request logic
├── activity_main.xml                - UI with permission buttons
└── MyVpnService.java               - VPN service (uses permissions)

Documentation Files:
├── README.md                        - Project overview
├── PERMISSIONS_GUIDE.md            - Detailed documentation
└── PERMISSION_FLOW.md              - Flow diagrams and testing
```

## Three-Step Implementation

### Step 1: Declare Permissions (AndroidManifest.xml)
```xml
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<service android:name=".MyVpnService"
         android:permission="android.permission.BIND_VPN_SERVICE">
    <intent-filter>
        <action android:name="android.net.VpnService" />
    </intent-filter>
</service>
```

### Step 2: Request at Runtime (MainActivity.java)
```java
// When user needs the feature:
requestPhonePermission();
requestVpnPermission();
```

### Step 3: Handle Result (MainActivity.java)
```java
// In callback methods:
onRequestPermissionsResult() → Handle phone permission
onActivityResult() → Handle VPN permission
```

## Common Code Patterns

### Check if Permission is Granted
```java
boolean hasPermission = ContextCompat.checkSelfPermission(
    context, 
    Manifest.permission.READ_PHONE_STATE
) == PackageManager.PERMISSION_GRANTED;
```

### Check if Should Show Rationale
```java
boolean shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
    activity,
    Manifest.permission.READ_PHONE_STATE
);
```

### Request Permission
```java
ActivityCompat.requestPermissions(
    activity,
    new String[]{Manifest.permission.READ_PHONE_STATE},
    REQUEST_CODE
);
```

### Check VPN Permission
```java
Intent intent = VpnService.prepare(context);
boolean hasVpnPermission = (intent == null);
```

## User Flow

```
1. User opens app
   ↓
2. Sees permission status (Not Granted)
   ↓
3. Taps "Request Phone Permission"
   ↓
4. Sees rationale dialog (optional)
   ↓
5. Sees system permission dialog
   ↓
6. Grants or denies
   ↓
7. Taps "Request VPN Permission"
   ↓
8. Sees system VPN dialog
   ↓
9. Allows or cancels
   ↓
10. Ready to use VPN (if both granted)
```

## Testing Commands

### Check Permissions via ADB
```bash
# List all permissions for the app
adb shell dumpsys package com.example.vpn | grep permission

# Revoke phone permission
adb shell pm revoke com.example.vpn android.permission.READ_PHONE_STATE

# Grant phone permission
adb shell pm grant com.example.vpn android.permission.READ_PHONE_STATE

# Clear app data (reset permissions)
adb shell pm clear com.example.vpn
```

### Build and Install
```bash
# Build debug APK
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Build and install in one command
./gradlew installDebug
```

## Permission States Matrix

| Phone Permission | VPN Permission | App State |
|-----------------|----------------|-----------|
| ❌ Not Granted  | ❌ Not Granted | Cannot use VPN |
| ✅ Granted      | ❌ Not Granted | Cannot use VPN |
| ❌ Not Granted  | ✅ Granted     | Limited VPN |
| ✅ Granted      | ✅ Granted     | ✅ Fully functional |

## Important Constants

```java
// Request codes
private static final int REQUEST_CODE_PHONE_STATE = 100;
private static final int REQUEST_CODE_VPN = 101;

// Permissions
Manifest.permission.READ_PHONE_STATE
Manifest.permission.INTERNET
Manifest.permission.ACCESS_NETWORK_STATE
Manifest.permission.CHANGE_NETWORK_STATE
```

## Key Classes and Methods

### Android Framework
- `ActivityCompat.checkSelfPermission()` - Check permission status
- `ActivityCompat.requestPermissions()` - Request runtime permission
- `ActivityCompat.shouldShowRequestPermissionRationale()` - Check if rationale needed
- `VpnService.prepare()` - Check/request VPN permission
- `onRequestPermissionsResult()` - Receive permission result
- `onActivityResult()` - Receive VPN permission result

### In This Project
- `MainActivity.requestPhonePermission()` - Request phone permission
- `MainActivity.requestVpnPermission()` - Request VPN permission
- `MainActivity.updatePermissionStatus()` - Update UI display
- `MyVpnService` - VPN service implementation

## Quick Troubleshooting

| Problem | Solution |
|---------|----------|
| Permission dialog not showing | Check targetSdkVersion ≥ 23 |
| VPN dialog not showing | Check manifest has BIND_VPN_SERVICE |
| Permission still denied after grant | Check if using correct context |
| "Don't ask again" selected | Guide user to app Settings |
| App crashes on permission use | Always check permission before use |

## Resources

- **MainActivity.java** - Main implementation (lines 59-97 for phone, 97-117 for VPN)
- **AndroidManifest.xml** - Permission declarations (lines 6-11)
- **PERMISSIONS_GUIDE.md** - Full documentation
- **PERMISSION_FLOW.md** - Detailed flows and diagrams

## Minimal Working Example

```java
public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    
    // Request permission
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
            new String[]{Manifest.permission.READ_PHONE_STATE},
            REQUEST_CODE);
    }
    
    // Handle result
    @Override
    public void onRequestPermissionsResult(int requestCode, 
            String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            }
        }
    }
}
```

## Next Steps After Implementation

1. ✅ Test on Android 6.0+ devices
2. ✅ Test permission denial scenarios
3. ✅ Test "Don't ask again" scenario
4. ✅ Verify VPN permission flow
5. ✅ Test app after permissions granted
6. ✅ Verify UI updates correctly
7. ✅ Test lifecycle (close/reopen app)

## Answer to Original Question

**"How phone permission will ask from user?"**

The phone permission (READ_PHONE_STATE) is requested from the user through:

1. **Declaration** in AndroidManifest.xml
2. **Runtime Request** using ActivityCompat.requestPermissions()
3. **System Dialog** shown to user by Android
4. **Result Handling** in onRequestPermissionsResult()

The implementation in this project shows the complete flow with:
- Rationale dialogs explaining why permission is needed
- Proper handling of grant/deny scenarios
- UI feedback showing permission status
- Guidance for users who permanently deny permissions

See `MainActivity.java` (lines 59-148) for the complete implementation.
