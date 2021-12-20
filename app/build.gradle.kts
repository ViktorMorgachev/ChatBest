import com.pet.buildsrc.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}


android {
    compileSdk = 31

    defaultConfig {
        applicationId = "com.pet.chat"
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
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro")
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

kapt {
    useBuildCache = true
    correctErrorTypes = true
}

dependencies {
    implementation(Libs.androidx_core_ktx)
    implementation(Libs.compose_ui)
    implementation(Libs.compose_material)
    implementation(Libs.compose_ui_tooling_preview)
    implementation(Libs.androidx_lifecycle_runtime_ktx)
    implementation(Libs.compose_activity)
    implementation(Libs.compose_material_icons_extended)
    implementation(Libs.compose_constraintlayout)
    implementation(Libs.socket_io_client) {
        exclude(group = "org.json", module = "json")
    }

    implementation(Libs.com_google_dagger_hilt)
    implementation(Libs.retrofit2)
    implementation(Libs.work_runtime_ktx)
    implementation(Libs.google_code_gson)
    implementation(Libs.fragment_ktx)
    implementation(Libs.compose_navigation)
    implementation(Libs.hilt_navigation_compose)
    implementation(Libs.activity_ktx)
    implementation(Libs.hilt_work)
    implementation(Libs.logging_interceptors)
    kapt(Libs.kapt_hilt_work)
    kapt(Libs.kapt_com_google_dagger_hilt)
    androidTestImplementation(AndroidTestLibs.ext_junit)
    androidTestImplementation(AndroidTestLibs.android_test_espresso_core)
    androidTestImplementation(AndroidTestLibs.androidx_compose_ui_test_junit4)
    testImplementation(TestLibs.junit)
    debugImplementation(DebugLibs.compose_ui_tooling)

}