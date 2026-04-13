import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

android {
    namespace = "com.syed.classconnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.syed.classconnect"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "GEMINI_API_KEY", "\"${propOrEnv("gemini.api.key", "GEMINI_API_KEY")}\"")
        buildConfigField("String", "NEWS_API_KEY", "\"${propOrEnv("news.api.key", "NEWS_API_KEY")}\"")
        buildConfigField("String", "FCM_SERVER_KEY", "\"${propOrEnv("fcm.server.key", "FCM_SERVER_KEY")}\"")
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "false"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["crashlyticsCollectionEnabled"] = "true"
        }

    }

    // Upload native (.so) debug symbols to Firebase Crashlytics on release builds.
    // This resolves the Play Console warning:
    // "This App Bundle contains native code, and you've not uploaded debug symbols."
    firebaseCrashlytics {
        nativeSymbolUploadEnabled = true
        unstrippedNativeLibsDir = "build/intermediates/merged_native_libs/release"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.process)

    // Activity / Fragment KTX
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)

    // Navigation
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)

    // Hilt DI
    implementation(libs.hilt.android)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    ksp(libs.hilt.compiler)

    // Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.crashlytics.ndk)   // NDK: upload .so debug symbols to Crashlytics
    implementation(libs.firebase.analytics.ktx)

    // Google Sign-In
    implementation(libs.gms.auth)

    // Retrofit + OkHttp
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    // Coroutines
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.play.services)

    // Glide
    implementation(libs.glide)
    ksp(libs.glide.compiler)

    // CircleImageView
    implementation(libs.circleimageview)

    // Lottie
    implementation(libs.lottie)

    // MPAndroidChart
    implementation(libs.mpandroidchart)

    // ZXing
    implementation(libs.zxing.core)
    implementation(libs.zxing.android)

    // CameraX + ML Kit
    implementation(libs.camera.camera2)
    implementation(libs.camera.lifecycle)
    implementation(libs.camera.view)
    implementation(libs.mlkit.barcode)

    // Shimmer
    implementation(libs.shimmer)

    // Markwon
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:ext-strikethrough:4.6.2")

    // Biometric
    implementation(libs.biometric)

    // Security
    implementation(libs.security.crypto)

    // DataStore
    implementation(libs.datastore.preferences)

    // ViewPager2
    implementation(libs.viewpager2)

    // SwipeRefreshLayout
    implementation(libs.swiperefreshlayout)

    // WorkManager
    implementation(libs.work.runtime.ktx)

    // Timber
    implementation(libs.timber)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)
}