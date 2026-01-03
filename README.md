# VPN - Android Permission Request Demo

An Android VPN application that demonstrates the proper implementation of phone permission requests from users.

## 📱 What This App Demonstrates

This project shows how to correctly implement permission requests in an Android VPN application, specifically:

1. **Runtime Permission Request** - How to ask for `READ_PHONE_STATE` permission
2. **VPN Service Authorization** - How to request VPN connection permission
3. **Permission Rationale** - How to explain why permissions are needed
4. **User Experience** - How to provide clear feedback about permission status
5. **Graceful Handling** - How to handle permission denials appropriately

## 🔐 Permissions Explained

### Phone State Permission (READ_PHONE_STATE)
- **Type**: Dangerous permission (requires runtime request on Android 6.0+)
- **Purpose**: Detect network changes to maintain stable VPN connections
- **User Action**: Must explicitly grant through system dialog

### VPN Service Permission
- **Type**: Special system permission
- **Purpose**: Allow app to establish VPN connections
- **User Action**: Must authorize through system VPN dialog

### Network Permissions
- **Type**: Normal permissions (auto-granted at install)
- **Included**: INTERNET, ACCESS_NETWORK_STATE, CHANGE_NETWORK_STATE

## 🚀 Quick Start

### Prerequisites
- Android Studio Arctic Fox or newer
- Android SDK (API 21+)
- Android device or emulator

### Installation
1. Clone this repository
2. Open in Android Studio
3. Build and run on your device/emulator
4. Follow on-screen prompts to grant permissions

## 📖 Documentation

See [PERMISSIONS_GUIDE.md](PERMISSIONS_GUIDE.md) for detailed documentation about:
- How permissions are requested
- Implementation details
- Code structure
- Testing scenarios
- Best practices

## 🏗️ Project Structure

```
vpn/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/vpn/
│   │   │   ├── MainActivity.java       # Permission request logic
│   │   │   └── MyVpnService.java      # VPN service
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   └── activity_main.xml  # UI layout
│   │   │   └── values/
│   │   │       └── strings.xml        # Resources
│   │   └── AndroidManifest.xml        # Permissions declaration
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── PERMISSIONS_GUIDE.md               # Detailed documentation
```

## 💡 Key Features

- ✅ Proper runtime permission request flow
- ✅ Permission rationale dialogs
- ✅ Real-time permission status display
- ✅ Handles permission denials gracefully
- ✅ Guides users to Settings for permanent denials
- ✅ Clean, intuitive user interface
- ✅ Follows Android best practices
- ✅ Comprehensive code comments

## 🎯 How It Works

1. **App Launch**: Displays current permission status
2. **Request Phone Permission**: Shows rationale, then system dialog
3. **Handle Response**: Updates UI based on user's choice
4. **Request VPN Permission**: Shows system VPN authorization
5. **Ready to Connect**: All permissions granted, ready for VPN

## 🔍 Testing

The app includes visual feedback for testing different scenarios:
- Fresh install (no permissions)
- Granting permissions
- Denying permissions
- Permanent denial ("Don't ask again")
- Returning from Settings

## 📱 Screenshots

The app displays:
- Two buttons to request different permissions
- Real-time permission status with emoji indicators
- Clear messages about what each permission does

## 🛠️ Building

```bash
# Build debug version
./gradlew assembleDebug

# Install on device
./gradlew installDebug

# Build release version
./gradlew assembleRelease
```

## 📚 Learning Resources

This project demonstrates concepts from:
- [Android Permissions Documentation](https://developer.android.com/guide/topics/permissions/overview)
- [VpnService API Reference](https://developer.android.com/reference/android/net/VpnService)
- [Runtime Permissions Best Practices](https://developer.android.com/training/permissions/requesting)

## 🤝 Contributing

This is a demonstration project. Feel free to use it as a reference for your own VPN applications.

## 📄 License

This project is provided as-is for educational purposes.