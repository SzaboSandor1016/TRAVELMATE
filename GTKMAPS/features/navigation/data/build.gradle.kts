plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.features.navigation.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(project(":features:navigation:domain"))
    implementation(project(":core:utils"))
    implementation(project(":app:location:domain"))
    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources:routedatasource:domain"))

    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.9.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")

    implementation("io.insert-koin:koin-android:4.1.0")
}