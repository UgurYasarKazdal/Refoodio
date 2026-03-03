import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.refoodio.core.data"
    compileSdk {
        version = release(36)
    }
    buildFeatures {
        buildConfig = true
    }
    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")


        val properties = Properties()
        val file = rootProject.file("local.properties")
        if (file.exists()) { properties.load(file.inputStream()) }

        buildConfigField("String", "GEMINI_API_KEY", "\"${properties.getProperty("GEMINI_API_KEY")}\"")

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
}

dependencies {
    // 1. Üst modülden gelen miras (ktx, coroutines, serialization buradan geliyor)
    implementation(project(":core"))

    // 2. Data Layer araçları
    implementation(libs.androidx.datastore)
    implementation(libs.androidx.startup)

    // 3. Bağımlılık Enjeksiyonu (Hilt kütüphanesi eklendi)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // 4. Katman bağlantıları
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))

    // 5. Testler
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}