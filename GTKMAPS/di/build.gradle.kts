plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
    /*dependency injection plugin*/
}

android {
    namespace = "com.example.di"
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

    implementation(project(":appnavigation"))
    implementation(project(":app:location:data"))
    implementation(project(":app:location:domain"))
    implementation(project(":core:auth:data"))
    implementation(project(":core:auth:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:database:dao"))
    implementation(project(":core:database:data"))
    implementation(project(":core:database:domain"))
    implementation(project(":core:remotedatasources"))
    implementation(project(":core:remotedatasources:reversegeocode:data"))
    implementation(project(":core:remotedatasources:reversegeocode:domain"))
    implementation(project(":core:remotedatasources:routedatasource:data"))
    implementation(project(":core:remotedatasources:routedatasource:domain"))
    implementation(project(":core:remotedatasources:searchplacesdatasource:data"))
    implementation(project(":core:remotedatasources:searchplacesdatasource:domain"))
    implementation(project(":core:remotedatasources:searchstartdatasource:data"))
    implementation(project(":core:remotedatasources:searchstartdatasource:domain"))
    implementation(project(":core:remotedatasources:tripremotedatasource:data"))
    implementation(project(":core:remotedatasources:tripremotedatasource:domain"))
    implementation(project(":core:utils"))
    implementation(project(":features:findcustom:data"))
    implementation(project(":features:findcustom:domain"))
    implementation(project(":features:findcustom:presentation"))
    implementation(project(":features:inspect:data"))
    implementation(project(":features:inspect:domain"))
    implementation(project(":features:inspect:presentation"))
    implementation(project(":features:navigation:data"))
    implementation(project(":features:navigation:domain"))
    implementation(project(":features:navigation:presentation"))
    implementation(project(":features:route:data"))
    implementation(project(":features:route:domain"))
    implementation(project(":features:route:presentation"))
    implementation(project(":features:savetrip:data"))
    implementation(project(":features:savetrip:domain"))
    implementation(project(":features:savetrip:presentation"))
    implementation(project(":features:search:data"))
    implementation(project(":features:search:domain"))
    implementation(project(":features:search:presentation"))
    implementation(project(":features:selectedplace:data"))
    implementation(project(":features:selectedplace:domain"))
    implementation(project(":features:selectedplace:presentation"))
    implementation(project(":features:selectedplaceoptions:data"))
    implementation(project(":features:selectedplaceoptions:domain"))
    implementation(project(":features:trips:data"))
    implementation(project(":features:trips:domain"))
    implementation(project(":features:trips:presentation"))
    implementation(project(":features:user:data"))
    implementation(project(":features:user:domain"))
    implementation(project(":features:user:presentation"))

    implementation("io.insert-koin:koin-android:4.1.0")
}