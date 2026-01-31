# Quick Start: Get Your Debug APK

## 🎯 Immediate Build Options

Since the dev container has memory limitations (2GB RAM), here are your options to get the debug APK:

---

## Option 1: Use GitHub Actions (Recommended - No local build needed)

### Steps:
1. **Push any commit** to `main` or `develop` branch
2. Go to **Actions** tab on GitHub
3. Wait for workflow to complete (~5-10 minutes)
4. Click on the workflow run
5. Download **debug-apk** artifact from the bottom
6. Unzip and install on your device

**Advantages:**
- ✅ No local setup required
- ✅ Always uses latest code
- ✅ Runs all quality checks automatically
- ✅ Consistent build environment

---

## Option 2: Use Android Studio

### Steps:
1. Open project in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**
4. Wait for build (2-5 minutes)
5. Click "locate" link in notification
6. APK location: `app/build/outputs/apk/development/debug/`

**Advantages:**
- ✅ Visual IDE
- ✅ Better memory management
- ✅ Easy debugging

---

## Option 3: Local Machine with 4GB+ RAM

### Prerequisites:
- JDK 17
- Android SDK
- 4GB+ RAM
- Linux/macOS/Windows

### Steps:
```bash
# Clone repository
git clone <your-repo-url>
cd finance-tracker

# Build debug APK
./gradlew assembleDevelopmentDebug

# APK location
app/build/outputs/apk/development/debug/app-development-debug.apk

# Install on connected device
adb install app/build/outputs/apk/development/debug/app-development-debug.apk
```

**Advantages:**
- ✅ Full control
- ✅ Fastest iteration
- ✅ Can run all tasks

---

## Option 4: Increase Dev Container Memory

### Edit `.devcontainer/devcontainer.json`:
```json
{
  "name": "Finance Tracker",
  "image": "mcr.microsoft.com/devcontainers/android:latest",
  "hostRequirements": {
    "memory": "8gb"
  }
}
```

### Rebuild container:
1. Command Palette (Ctrl+Shift+P)
2. "Dev Containers: Rebuild Container"
3. Wait for rebuild
4. Run: `./gradlew assembleDevelopmentDebug`

**Advantages:**
- ✅ Work in same environment
- ✅ Once setup works for all builds

**Disadvantages:**
- ⚠️ Requires host machine with 8GB+ RAM
- ⚠️ Slower than local builds

---

## 🚀 Fastest Path: GitHub Actions

**For immediate testing, use GitHub Actions (Option 1):**

1. Make any small change (or empty commit):
   ```bash
   git commit --allow-empty -m "Trigger build"
   git push origin main
   ```

2. Go to GitHub → Actions tab

3. Click on the workflow run "Android CI"

4. After ~5-10 minutes, download "debug-apk" artifact

5. Unzip and you have your APK:
   ```
   app-development-debug.apk
   ```

6. Install:
   - Transfer to Android device
   - Enable "Install from unknown sources"
   - Tap APK to install
   - Or use `adb install app-development-debug.apk`

---

## 📱 Installing APK on Android Device

### Method 1: ADB (Recommended)
```bash
# Connect device via USB with USB debugging enabled
adb devices  # Verify device connected

# Install APK
adb install app-development-debug.apk

# If already installed, reinstall
adb install -r app-development-debug.apk
```

### Method 2: Manual Installation
1. Transfer APK to device (USB/Email/Cloud)
2. On device: Settings → Security → Install from unknown sources (Enable)
3. Open File Manager
4. Navigate to APK location
5. Tap APK to install
6. Tap "Install"
7. Launch app from app drawer

### Method 3: Wireless ADB
```bash
# On device: Enable wireless debugging (Android 11+)
# Settings → Developer options → Wireless debugging

# On computer:
adb pair <IP>:<PORT>  # Enter pairing code from device
adb connect <IP>:<PORT>
adb install app-development-debug.apk
```

---

## 🔧 Troubleshooting

### "Gradle daemon disappeared"
**Cause**: Insufficient memory (< 4GB)
**Solution**: Use Option 1 (GitHub Actions) or Option 2 (Android Studio)

### "Build takes too long"
**Tip**: First build is slow (10-15 min), subsequent builds are faster (2-5 min)

### "Cannot find JDK"
```bash
# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
```

### "Android SDK not found"
```bash
# Set ANDROID_HOME
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$PATH
```

---

## 💡 Pro Tips

### Build All Variants
```bash
./gradlew assemble  # Builds all variants (takes longer)
```

### List Available Tasks
```bash
./gradlew tasks  # See all available Gradle tasks
```

### Build Specific Variant
```bash
./gradlew assembleDevelopmentDebug    # Dev debug
./gradlew assembleDevelopmentRelease  # Dev release (requires keystore)
./gradlew assembleProductionDebug     # Prod debug
./gradlew assembleProductionRelease   # Prod release (requires keystore)
```

### Check APK Size
```bash
ls -lh app/build/outputs/apk/development/debug/*.apk
# Should be 10-20 MB for debug build
```

---

## 🎁 Pre-Built APK (If Available)

If someone has already built the APK:

### From GitHub Releases
1. Go to **Releases** page
2. Download latest release APK
3. Install on device

### From GitHub Actions
1. Go to **Actions** tab
2. Find successful workflow run
3. Download **debug-apk** artifact
4. Unzip and install

---

## 📊 Build Status

| Build Variant | APK Size | Signing | ProGuard | Ready |
|---------------|----------|---------|----------|--------|
| developmentDebug | ~15 MB | Debug | No | ✅ Yes |
| developmentRelease | ~8 MB | Release | Yes | ⏳ Needs keystore |
| productionDebug | ~15 MB | Debug | No | ✅ Yes |
| productionRelease | ~8 MB | Release | Yes | ⏳ Needs keystore |

**For testing, use `developmentDebug` - no keystore required!**

---

## 🆘 Still Having Issues?

### Quick Help:
1. **Check GitHub Actions**: Is workflow failing?
2. **Check logs**: `./gradlew assembleDevelopmentDebug --info`
3. **Clean build**: `./gradlew clean`
4. **Stop daemons**: `./gradlew --stop`
5. **Use Android Studio**: Most reliable option

### Need More Help?
- Check [PRODUCTION_READY_STATUS.md](PRODUCTION_READY_STATUS.md) for detailed instructions
- Check [RELEASE_PIPELINE.md](RELEASE_PIPELINE.md) for release process
- Open an issue on GitHub

---

**Bottom Line: Use GitHub Actions (Option 1) to get your APK in 5-10 minutes with zero local setup! 🚀**
