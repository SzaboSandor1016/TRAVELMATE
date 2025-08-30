plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    /*dependency injection plugin*/
}

android {
    namespace = "com.example.features.search.domain"
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

    implementation(project(":app:location:domain"))
    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources:searchstartdatasource:domain"))
    implementation(project(":core:remotedatasources:searchplacesdatasource:domain"))
    implementation(project(":core:remotedatasources:reversegeocode:domain"))

    // Kotlin Coroutines (for Flow/StateFlow, etc.)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources"))

    // Optional: for testing domain logic
    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    implementation("io.insert-koin:koin-android:4.1.0")
}