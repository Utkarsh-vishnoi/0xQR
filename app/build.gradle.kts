plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "io.github.utkarshvishnoi.zeroxqr"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.github.utkarshvishnoi.zeroxqr"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["usesCleartextTraffic"] = "false"
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Security hardening for air-gapped deployment
            isDebuggable = false
            isJniDebuggable = false
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    // Security configuration for air-gapped environments
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // Remove any network-related resources
            excludes += "/META-INF/services/javax.xml.stream.*"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Biometric authentication (offline capable)
    implementation(libs.androidx.biometric)

    // Security and cryptography (native Android, no network dependencies)
    implementation(libs.androidx.security.crypto)

    // Navigation component for UI flow
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
}