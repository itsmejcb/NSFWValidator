plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.itsmejcb.nsfwvalidator"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.itsmejcb.nsfwvalidator"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.image.labeling)
    implementation(libs.image.labeling.common)
    implementation(libs.image.labeling.custom)
    implementation(libs.image.labeling.custom.common)
    implementation(libs.image.labeling.default.common)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.picasso.transformations)
    implementation(libs.picasso)
    implementation(libs.okhttp3)
    implementation(libs.gson)
    implementation(libs.retrofit2.rxjava2.adapter)
    implementation(libs.rxandroid)
    implementation(libs.rxjava)
    implementation(libs.adapter.rxjava2)
    implementation(libs.logging.interceptor)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.shimmer)
    implementation(libs.androidx.palette)
    implementation(libs.androidx.browser)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}