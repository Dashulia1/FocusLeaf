plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // id("com.google.gms.google-services")    // ← ДОБАВИЛА //
}

android {
    namespace = "com.dasasergeeva.focusleaf2"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dasasergeeva.focusleaf2"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // Поддержка русского и английского языков
        resourceConfigurations += listOf("ru", "en")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.1.0")
    implementation("com.google.code.gson:gson:2.10.1")
    
    // Lifecycle для корутин и StateFlow
    val lifecycleVersion = "2.6.2"
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // implementation(platform("com.google.firebase:firebase-bom:32.7.0"))        // ← ЕСТЬ //
    // implementation("com.google.firebase:firebase-auth")                         // ← ЕСТЬ //
    // implementation("com.google.firebase:firebase-firestore")                    // ← ЕСТЬ //
    // implementation("com.google.firebase:firebase-storage")                      // ← ЕСТЬ //
    // implementation("com.google.firebase:firebase-messaging")                    // ← ЕСТЬ //
    // implementation("com.google.firebase:firebase-analytics")                    // ← ЕСТЬ //
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}