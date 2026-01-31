# Expense Tracker - Setup and Run Instructions

## Prerequisites

### 1. Install Java Development Kit (JDK)
- **Minimum**: Java 17
- **Recommended**: Java 21 or later
- Verify installation:
  ```bash
  java -version
  ```

### 2. Install Android SDK
You have two options:

#### Option A: Download Android Studio (Recommended)
- Download from [android.com/studio](https://developer.android.com/studio)
- Install and follow the initial setup wizard
- The SDK will be automatically installed

#### Option B: Install Command-Line Tools Only
```bash
# Download Android SDK Command-line Tools
# Visit: https://developer.android.com/studio#command-tools

# Set ANDROID_SDK_ROOT environment variable
export ANDROID_SDK_ROOT=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin

# Install necessary SDK components
sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platforms;android-34"
sdkmanager --sdk_root=$ANDROID_SDK_ROOT "build-tools;34.0.0"
sdkmanager --sdk_root=$ANDROID_SDK_ROOT "platform-tools"
```

## Building the Project

### Step 1: Clone/Navigate to Project
```bash
cd /path/to/finance-tracker
```

### Step 2: Build the APK (Debug)
```bash
./gradlew clean build -x test
```

Or for faster builds (without cleaning):
```bash
./gradlew assembleDebug
```

### Step 3: Build the APK (Release)
```bash
./gradlew assembleRelease
```

## Running the App

### Option 1: Using Android Emulator
```bash
# Start Android emulator (must be configured in Android Studio first)
emulator -avd <emulator_name>

# Install and run the debug APK
./gradlew installDebug
```

### Option 2: Using Physical Device
1. Enable USB Debugging on your Android device
2. Connect via USB
3. Verify connection:
   ```bash
   adb devices
   ```
4. Install and run:
   ```bash
   ./gradlew installDebug
   ```

### Option 3: Run Directly (Requires Emulator/Device Connected)
```bash
./gradlew run
```

## Output Files

After successful build, APK files are located at:
- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

### Install APK Manually
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Build Troubleshooting

### Gradle Daemon Issues
```bash
./gradlew --stop
./gradlew clean build -x test --debug
```

### Dependency Download Issues
```bash
./gradlew --refresh-dependencies
```

### Clean Build
```bash
rm -rf .gradle build
./gradlew clean build -x test
```

### Clear Gradle Cache
```bash
rm -rf ~/.gradle/caches/
```

## Project Structure

```
finance-tracker/
├── app/                           # Android application module
│   ├── src/
│   │   └── main/
│   │       ├── kotlin/           # Kotlin source code
│   │       │   └── com/expense/tracker/
│   │       │       ├── data/     # Database entities, DAOs
│   │       │       ├── service/  # SMS receiver and handler
│   │       │       ├── ui/       # Jetpack Compose screens
│   │       │       ├── repository/
│   │       │       ├── utils/    # Utility functions
│   │       │       └── MainActivity.kt
│   │       ├── res/              # Resources (strings, colors, etc.)
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts          # App-level build configuration
├── gradle/wrapper/               # Gradle wrapper files
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
└── gradle.properties             # Gradle properties
```

## Key Features

- ✅ Automatic SMS Detection: Monitors incoming SMS and auto-categorizes expenses
- ✅ CRUD Operations: Add, edit, delete, and view expenses
- ✅ Smart Categorization: Auto-categorizes based on merchant keywords
- ✅ Reports & Analytics: Monthly and yearly spending reports
- ✅ Advanced Filtering: Filter by date, category, amount, payment mode
- ✅ Export Functionality: Export to PDF and CSV formats
- ✅ Offline-First: All data stored locally using Room database
- ✅ Modern UI: Built with Jetpack Compose and Material Design 3

## App Permissions

The app requires the following permissions:

| Permission | Purpose |
|-----------|---------|
| READ_SMS | Read incoming SMS messages |
| RECEIVE_SMS | Receive SMS broadcast |
| SEND_SMS | Send SMS (if needed) |
| READ_CONTACTS | Auto-fill sender details |
| POST_NOTIFICATIONS | Show notifications |
| INTERNET | Cloud sync (future feature) |
| WRITE_EXTERNAL_STORAGE | Export files |

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests (on device/emulator)
```bash
./gradlew connectedAndroidTest
```

## Development

### Using Android Studio
1. Open project: `File > Open > Select project folder`
2. Wait for Gradle sync to complete
3. Click "Run" (Shift + F10) to build and run

### Using Command Line with VS Code
1. Install Android SDK Tools extension
2. Set up emulator using Android Studio
3. Use above gradle commands to build
4. Deploy APK using `adb install`

## Gradle Commands Reference

| Command | Purpose |
|---------|---------|
| `./gradlew build` | Full build (compile + test + package) |
| `./gradlew assembleDebug` | Build debug APK only |
| `./gradlew assembleRelease` | Build release APK only |
| `./gradlew installDebug` | Install debug APK to device |
| `./gradlew run` | Build and run on connected device |
| `./gradlew test` | Run unit tests |
| `./gradlew clean` | Clean build artifacts |
| `./gradlew --version` | Show Gradle version |

## Next Steps

1. **Install Android SDK** using the prerequisites section
2. **Run the build command**: `./gradlew assembleDebug`
3. **Start an emulator** or connect a physical device
4. **Install the APK**: `adb install app/build/outputs/apk/debug/app-debug.apk`
5. **Open the app** on your device and test the features

## Support

For detailed implementation documentation, see:
- [IMPLEMENTATION_GUIDE.md](IMPLEMENTATION_GUIDE.md) - Feature implementation details
- [API_REFERENCE.md](API_REFERENCE.md) - API and class documentation
- [PROJECT_SUMMARY.md](PROJECT_SUMMARY.md) - Complete project overview
