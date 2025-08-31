
plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.travel_mate"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.travel_mate"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "2025.8.16"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(project(":core:utils"))
    implementation(project(":core:ui"))
    implementation(project(":core:database"))
    implementation(project(":core:database:dao"))
    implementation(project(":core:database:data"))
    implementation(project(":core:database:domain"))

    implementation(project(":app:location:data"))
    implementation(project(":app:location:domain"))

    implementation(project(":di"))
    implementation(project(":appnavigation"))

    implementation(project(":features:selectedplaceoptions:domain"))

    implementation(project(":features:findcustom:presentation"))
    implementation(project(":features:inspect:presentation"))
    implementation(project(":features:navigation:presentation"))
    implementation(project(":features:search:presentation"))
    implementation(project(":features:route:presentation"))
    implementation(project(":features:selectedplace:presentation"))
    implementation(project(":features:user:presentation"))
    implementation(project(":features:savetrip:presentation"))

    implementation(project(":features:findcustom:domain"))
    implementation(project(":features:inspect:domain"))
    implementation(project(":features:navigation:domain"))
    implementation(project(":features:search:domain"))
    implementation(project(":features:route:domain"))
    implementation(project(":features:selectedplace:domain"))
    implementation(project(":features:savetrip:domain"))

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    implementation("org.osmdroid:osmdroid-android:6.1.18")
    implementation("pl.droidsonroids.gif:android-gif-drawable:1.2.29")

    implementation("androidx.recyclerview:recyclerview:1.4.0")

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.0")

    implementation("androidx.activity:activity-ktx:1.10.1")

    val nav_version = "2.9.3"

    api("androidx.navigation:navigation-fragment-ktx:$nav_version")
    api("androidx.navigation:navigation-ui-ktx:$nav_version")
    api("androidx.navigation:navigation-dynamic-features-fragment:$nav_version")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")

    implementation ("com.github.MKergall:osmbonuspack:6.9.0")

    val room_version = "2.7.1"

    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")


    val fragment_version = "1.8.6"
    // Kotlin
    implementation("androidx.fragment:fragment-ktx:$fragment_version")

    implementation("io.insert-koin:koin-android:4.1.0")
}
