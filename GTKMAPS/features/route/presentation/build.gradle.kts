plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    /*dependency injection plugin*/
}

android {
    namespace = "com.example.features.route.presentation"
    compileSdk = 35

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
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

    implementation(project(":core:ui"))
    implementation(project(":core:utils"))

    implementation(project(":features:route:domain"))
    implementation(project(":features:selectedplace:domain"))
    implementation(project(":features:navigation:domain"))
    implementation(project(":features:search:domain"))
    implementation(project(":features:inspect:domain"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")

    implementation("androidx.recyclerview:recyclerview:1.4.0")

    val fragment_version = "1.8.6"
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    implementation("io.insert-koin:koin-android:4.1.0")
}