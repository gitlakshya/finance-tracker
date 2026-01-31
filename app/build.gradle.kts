plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    // Detekt temporarily disabled due to memory constraints
    // id("io.gitlab.arturbosch.detekt") version "1.23.4"
}

android {
    namespace = "com.expense.tracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.expense.tracker"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // BuildConfig fields
        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
        buildConfigField("int", "VERSION_CODE", "${versionCode}")
        buildConfigField("String", "BUILD_TIME", "\"${System.currentTimeMillis()}\"")
    }

    signingConfigs {
        create("release") {
            // Store signing config in gradle.properties or use environment variables
            storeFile = file(project.findProperty("RELEASE_STORE_FILE") as String? ?: "release-keystore.jks")
            storePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String? ?: System.getenv("RELEASE_STORE_PASSWORD")
            keyAlias = project.findProperty("RELEASE_KEY_ALIAS") as String? ?: System.getenv("RELEASE_KEY_ALIAS")
            keyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as String? ?: System.getenv("RELEASE_KEY_PASSWORD")
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isMinifyEnabled = false
        }
        
        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        
        create("staging") {
            initWith(getByName("release"))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            isDebuggable = true
            matchingFallbacks += listOf("debug")
        }
    }
    
    flavorDimensions += "environment"
    productFlavors {
        create("production") {
            dimension = "environment"
            buildConfigField("String", "API_BASE_URL", "\"https://api.expensetracker.com\"")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
        }
        
        create("development") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            buildConfigField("String", "API_BASE_URL", "\"https://dev-api.expensetracker.com\"")
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    lint {
        checkReleaseBuilds = false
        abortOnError = false
        disable += setOf(
            "VectorPath",
            "IconMissingDensityFolder",
            "IconDensities",
            "ObsoleteLintCustomCheck"
        )
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    
    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.4")
    
    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // JSON
    implementation("com.google.code.gson:gson:2.10.1")
    
    // PDF
    implementation("com.itextpdf:itextpdf:5.5.13.3")
    
    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:3.1.0")
    
    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.33.1-alpha")
    
    // Calendar (for calendar view)
    implementation("com.kizitonwose.calendar:compose:2.4.1")
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito:mockito-inline:5.2.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("androidx.room:room-testing:2.6.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    
    // Code quality - Detekt temporarily disabled due to memory constraints
    // detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.4")
}

// Detekt configuration - temporarily disabled due to memory constraints
// Will be enabled in CI/CD with adequate resources
// detekt {
//     buildUponDefaultConfig = true
//     allRules = false
//     config.setFrom("$projectDir/config/detekt/detekt.yml")
//     baseline = file("$projectDir/config/detekt/baseline.xml")
// }
//
// tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
//     reports {
//         html.required.set(true)
//         xml.required.set(true)
//         txt.required.set(false)
//         sarif.required.set(true)
//     }
// }
//
// tasks.withType<io.gitlab.arturbosch.detekt.DetektCreateBaselineTask>().configureEach {
//     description = "Overrides current baseline."
//     buildUponDefaultConfig.set(true)
//     ignoreFailures.set(true)
//     parallel.set(true)
//     setSource(files(rootDir))
//     config.setFrom(files("$projectDir/config/detekt/detekt.yml"))
//     baseline.set(file("$projectDir/config/detekt/baseline.xml"))
//     include("**/*.kt")
//     include("**/*.kts")
//     exclude("**/resources/**")
//     exclude("**/build/**")
// }
