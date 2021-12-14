pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version ("7.1.0-alpha10")
        id("com.android.library") version ("7.1.0-alpha10")
        id("org.jetbrains.kotlin.android") version ("1.5.10")
        id("org.jetbrains.kotlin.jvm") version ("1.5.10")
        id("org.jetbrains.kotlin.kapt") version ("1.5.10")
        id("dagger.hilt.android.plugin") version ("2.39.1")
    }
    resolutionStrategy {
        eachPlugin {
            if( requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:2.39.1")
            }
        }
    }
}


dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "LoveFinder"
include(":app")
