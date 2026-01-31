# Release Pipeline Documentation

## Overview
This document outlines the complete release pipeline strategy for the Expense Tracker Android application.

## Build Variants

### Environments
- **Production**: Production-ready builds for Play Store release
- **Development**: Development builds with debug features enabled

### Build Types
- **Debug**: Development builds with debugging enabled
  - Application ID suffix: `.debug`
  - Version suffix: `-debug`
  - Minification: Disabled
  - Debuggable: Yes

- **Release**: Production builds
  - Minification: Enabled (ProGuard)
  - Resource shrinking: Enabled
  - Debuggable: No
  - Signed with release keystore

- **Staging**: Pre-production testing builds
  - Application ID suffix: `.staging`
  - Version suffix: `-staging`
  - Minification: Enabled
  - Debuggable: Yes (for testing)

### Build Variants Matrix
| Variant | App ID | Environment | Use Case |
|---------|--------|-------------|----------|
| productionDebug | com.expense.tracker.debug | Production | Local development |
| productionRelease | com.expense.tracker | Production | Play Store release |
| productionStaging | com.expense.tracker.staging | Production | UAT testing |
| developmentDebug | com.expense.tracker.dev.debug | Development | Feature development |
| developmentRelease | com.expense.tracker.dev | Development | Internal testing |

## Signing Configuration

### Keystore Setup

1. **Generate Release Keystore** (one-time):
   ```bash
   keytool -genkey -v -keystore release-keystore.jks \
     -alias expense-tracker-release \
     -keyalg RSA -keysize 2048 -validity 10000
   ```

2. **Store Keystore Securely**:
   - Never commit keystore to version control
   - Add to `.gitignore`
   - Store in secure location (password manager, CI secrets)

3. **Configure Signing Credentials**:
   
   **Option A: gradle.properties (local development)**
   ```properties
   RELEASE_STORE_FILE=../path/to/release-keystore.jks
   RELEASE_STORE_PASSWORD=your_store_password
   RELEASE_KEY_ALIAS=expense-tracker-release
   RELEASE_KEY_PASSWORD=your_key_password
   ```

   **Option B: Environment Variables (CI/CD)**
   ```bash
   export RELEASE_STORE_FILE=release-keystore.jks
   export RELEASE_STORE_PASSWORD=your_store_password
   export RELEASE_KEY_ALIAS=expense-tracker-release
   export RELEASE_KEY_PASSWORD=your_key_password
   ```

## Version Management

### Version Code
- Incremented with each release
- Format: Integer (1, 2, 3, ...)
- Used by Play Store to identify updates

### Version Name
- Semantic versioning: `MAJOR.MINOR.PATCH`
- Format: `1.0.0`, `1.1.0`, `2.0.0`
- Displayed to users

### Versioning Strategy
```
1.0.0 - Initial release
1.0.1 - Bug fixes
1.1.0 - New features (backward compatible)
2.0.0 - Breaking changes / Major updates
```

## Release Process

### 1. Pre-Release Checklist
- [ ] All tests passing
- [ ] Code review completed
- [ ] Version numbers updated
- [ ] Changelog updated
- [ ] Release notes prepared
- [ ] ProGuard rules validated
- [ ] App size optimized
- [ ] Permissions reviewed
- [ ] Privacy policy updated

### 2. Build Generation

**Manual Build:**
```bash
# Clean build
./gradlew clean

# Generate production release APK
./gradlew assembleProductionRelease

# Generate AAB (Android App Bundle) for Play Store
./gradlew bundleProductionRelease

# Output locations:
# APK: app/build/outputs/apk/production/release/app-production-release.apk
# AAB: app/build/outputs/bundle/productionRelease/app-production-release.aab
```

**Staging Build:**
```bash
./gradlew assembleProductionStaging
```

### 3. Testing

**Local Testing:**
```bash
# Install release build on device
adb install app/build/outputs/apk/production/release/app-production-release.apk

# Install staging build
adb install app/build/outputs/apk/production/staging/app-production-staging.apk
```

**Automated Testing:**
```bash
# Unit tests
./gradlew testProductionReleaseUnitTest

# Instrumented tests
./gradlew connectedProductionReleaseAndroidTest
```

### 4. Play Store Release

**Tracks:**
- **Internal Testing**: Immediate access for internal testers
- **Closed Testing (Alpha)**: Limited external testers
- **Open Testing (Beta)**: Public beta program
- **Production**: All users

**Release Workflow:**
1. Upload AAB to Play Console
2. Internal testing (1-2 days)
3. Closed testing (3-7 days)
4. Open testing (optional, 7-14 days)
5. Staged rollout to production:
   - 5% for 24 hours
   - 10% for 24 hours
   - 25% for 48 hours
   - 50% for 48 hours
   - 100% (full rollout)

### 5. Post-Release
- Monitor crash reports (Firebase Crashlytics)
- Track ANR (Application Not Responding) rates
- Monitor Play Store reviews
- Track key metrics (retention, engagement)
- Prepare hotfix if critical issues found

## CI/CD Pipeline

### GitHub Actions Workflow

**File**: `.github/workflows/android-release.yml`

```yaml
name: Android Release

on:
  push:
    tags:
      - 'v*'
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Decode Keystore
        env:
          ENCODED_KEYSTORE: ${{ secrets.RELEASE_KEYSTORE_BASE64 }}
        run: echo $ENCODED_KEYSTORE | base64 -di > release-keystore.jks

      - name: Build Release APK
        env:
          RELEASE_STORE_FILE: release-keystore.jks
          RELEASE_STORE_PASSWORD: ${{ secrets.RELEASE_STORE_PASSWORD }}
          RELEASE_KEY_ALIAS: ${{ secrets.RELEASE_KEY_ALIAS }}
          RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
        run: ./gradlew assembleProductionRelease

      - name: Build Release AAB
        env:
          RELEASE_STORE_FILE: release-keystore.jks
          RELEASE_STORE_PASSWORD: ${{ secrets.RELEASE_STORE_PASSWORD }}
          RELEASE_KEY_ALIAS: ${{ secrets.RELEASE_KEY_ALIAS }}
          RELEASE_KEY_PASSWORD: ${{ secrets.RELEASE_KEY_PASSWORD }}
        run: ./gradlew bundleProductionRelease

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-production-release
          path: app/build/outputs/apk/production/release/app-production-release.apk

      - name: Upload AAB
        uses: actions/upload-artifact@v4
        with:
          name: app-bundle-production-release
          path: app/build/outputs/bundle/productionRelease/app-production-release.aab

      - name: Create GitHub Release
        uses: softprops/action-gh-release@v1
        with:
          files: |
            app/build/outputs/apk/production/release/app-production-release.apk
            app/build/outputs/bundle/productionRelease/app-production-release.aab
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}

  test:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Run Unit Tests
        run: ./gradlew testProductionReleaseUnitTest

      - name: Upload Test Reports
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: test-reports
          path: app/build/reports/tests/

  deploy:
    needs: [build, test]
    runs-on: ubuntu-latest
    if: startsWith(github.ref, 'refs/tags/v')
    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Download AAB
        uses: actions/download-artifact@v4
        with:
          name: app-bundle-production-release

      - name: Upload to Play Store
        uses: r0adkll/upload-google-play@v1
        with:
          serviceAccountJsonPlainText: ${{ secrets.PLAY_STORE_SERVICE_ACCOUNT }}
          packageName: com.expense.tracker
          releaseFiles: app-production-release.aab
          track: internal
          status: completed
```

### Required GitHub Secrets

Set these in GitHub repository settings > Secrets and variables > Actions:

```
RELEASE_KEYSTORE_BASE64      # Base64 encoded keystore file
RELEASE_STORE_PASSWORD       # Keystore password
RELEASE_KEY_ALIAS           # Key alias
RELEASE_KEY_PASSWORD        # Key password
PLAY_STORE_SERVICE_ACCOUNT  # Play Console service account JSON
```

## Continuous Integration

### Pull Request Workflow

**File**: `.github/workflows/android-ci.yml`

```yaml
name: Android CI

on:
  pull_request:
    branches: [ main, develop ]
  push:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
          cache: 'gradle'

      - name: Grant execute permission for gradlew
        run: chmod +x gradlew

      - name: Run Lint
        run: ./gradlew lintDebug

      - name: Run Unit Tests
        run: ./gradlew testDebugUnitTest

      - name: Build Debug APK
        run: ./gradlew assembleDebug

      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
```

## ProGuard Configuration

Ensure proper ProGuard rules in `proguard-rules.pro`:

```proguard
# Keep Room entities
-keep class com.expense.tracker.data.model.** { *; }

# Keep Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep Compose
-keep class androidx.compose.** { *; }
-keepclassmembers class androidx.compose.** { *; }

# Keep Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
```

## Rollback Strategy

If critical issues are detected post-release:

1. **Immediate Actions**:
   - Halt staged rollout in Play Console
   - Prepare hotfix branch
   - Increment version code

2. **Hotfix Process**:
   ```bash
   git checkout -b hotfix/v1.0.1
   # Fix the issue
   git commit -m "Fix: Critical bug description"
   git tag v1.0.1
   git push origin hotfix/v1.0.1 --tags
   ```

3. **Emergency Release**:
   - Build and test hotfix
   - Fast-track through internal testing only
   - Release to affected percentage immediately

## Monitoring & Analytics

### Crash Reporting
- Integrate Firebase Crashlytics
- Monitor crash-free users percentage (target: >99.5%)
- Set up alerts for crash rate spikes

### Performance Monitoring
- Track app startup time
- Monitor ANR rate (target: <0.5%)
- Track memory usage

### Key Metrics
- Daily Active Users (DAU)
- Monthly Active Users (MAU)
- Retention rates (Day 1, Day 7, Day 30)
- Play Store rating (target: >4.0)

## Checklist for First Release

- [ ] App icon finalized
- [ ] Play Store listing completed
- [ ] Screenshots (min 2, recommended 8)
- [ ] Feature graphic (1024x500)
- [ ] App description and keywords
- [ ] Privacy policy URL
- [ ] Content rating questionnaire
- [ ] Target audience and age restrictions
- [ ] App category selected
- [ ] Pricing set (free/paid)
- [ ] Countries/regions selected
- [ ] Release notes prepared
- [ ] Support email configured
- [ ] Website URL (optional)

## Resources

- [Android App Bundle Documentation](https://developer.android.com/guide/app-bundle)
- [Play Console Help](https://support.google.com/googleplay/android-developer)
- [ProGuard Documentation](https://www.guardsquare.com/manual/home)
- [GitHub Actions for Android](https://github.com/actions/setup-java)
