import com.pet.buildsrc.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}



android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.pet.lovefinder"
        minSdk = 21
        targetSdk = 31
        versionCode = 1
        versionName = "1.0"

        //testInstrumentationRunner("androidx.test.runner.AndroidJUnitRunner")
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose_version
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(Libs.androidx_core_ktx)
    implementation(Libs.compose_ui)
    implementation(Libs.compose_material)
    implementation(Libs.compose_ui_tooling_preview)
    implementation(Libs.androidx_lifecycle_runtime_ktx)
    implementation(Libs.android_activity_compose)
    implementation(Libs.compose_material_icons_extended)
    testImplementation(TestLibs.junit)
    androidTestImplementation(AndroidTestLibs.ext_junit)
    androidTestImplementation(AndroidTestLibs.android_test_espresso_core)
    androidTestImplementation(AndroidTestLibs.androidx_compose_ui_test_junit4)
    debugImplementation(DebugLibs.compose_ui_tooling)
}