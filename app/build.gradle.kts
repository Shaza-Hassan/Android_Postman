import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
}

android {
    namespace = "com.shaza.androidpostman"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.shaza.androidpostman"
        minSdk = 28
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"  // or "1.8" depending on your target JVM version
    }

    testOptions{
        animationsDisabled = true
        unitTests.isIncludeAndroidResources = true
        packaging {
            jniLibs {
                useLegacyPackaging = true
            }
        }

    }

    packaging{
        resources.excludes.add("META-INF/LICENSE.md")
        resources.excludes.add("META-INF/LICENSE")
        resources.excludes.add("META-INF/ASL2.0")
        resources.excludes.add("META-INF/NOTICE")
        resources.excludes.add("META-INF/LICENSE-notice.md") // Add this line to exclude the conflicting file

    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.espresso.contrib)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.mockk.agent)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    debugImplementation(libs.mockk)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.fragment.testing)
    androidTestImplementation(libs.androidx.uiautomator)
    debugImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.espresso.core)
    debugImplementation(libs.androidx.core.testing)
    debugImplementation(libs.androidx.junit.ktx)
    debugImplementation(libs.androidx.fragment.testing)
    debugImplementation(libs.androidx.uiautomator)
    androidTestImplementation(libs.mockk)
    debugImplementation(libs.mockk)
}