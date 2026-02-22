plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.dagger.hilt.android)
    alias(libs.plugins.ksp)

}

android {
    namespace = "com.refoodio"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.refoodio"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

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
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // UI ve Lifecycle (MainActivity için şart)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose (Sadece ekranları host etmek için gereken temel yapılar)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    // DI (Hilt) - Uygulamanın beyni
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Modüller
    implementation(project(":core:navigation"))
    implementation(project(":core:ui"))
    implementation(project(":core:data")) // DI graph'ı tamamlamak için

    // Feature modülleri (Compile-time güvenliği için runtimeOnly)
    runtimeOnly(project(":feature:inventory"))
    runtimeOnly(project(":feature:home"))
    runtimeOnly(project(":feature:recipe"))
    runtimeOnly(project(":feature:settings"))

    // Debug ve Test (İsteğe bağlı, app modülünde minimumda tutulmalı)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
