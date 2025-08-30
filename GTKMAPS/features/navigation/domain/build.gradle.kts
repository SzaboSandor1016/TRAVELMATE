plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    /*dependency injection plugin*/
}

android {
    namespace = "com.example.features.navigation.domain"
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
    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources:routedatasource:domain"))
    implementation(project(":features:route:domain"))

    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources:routedatasource:domain"))
    implementation(project(":features:route:domain"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")

    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")

    // Optional: for testing domain logic
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    implementation("io.insert-koin:koin-android:4.1.0")
}