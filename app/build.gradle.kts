import org.gradle.initialization.Environment
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}


android {
    namespace = "com.example.currencyconverter"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.currencyconverter"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val properties = Properties()
        properties.load(rootProject.file("secrets.properties").inputStream())
        val apiKey = properties.getProperty("exchangeRate_API_key") ?: ""
        buildConfigField(
            type = "String",
            name = "exchangeRate_API_key",
            value = apiKey
        )
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}



dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("org.json:json:20250517")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:5.3.0")
    implementation("androidx.core:core-splashscreen:1.2.0")
    testImplementation(libs.junit)
    testImplementation("androidx.test:core:1.7.0")
    testImplementation("org.mockito:mockito-core:5.21.0")
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}