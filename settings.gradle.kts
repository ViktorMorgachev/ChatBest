
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version("7.1.0-alpha10")
        id("com.android.library") version("7.1.0-alpha10")
        id("org.jetbrains.kotlin.android") version("1.5.10")
        id("org.jetbrains.kotlin.jvm") version("1.5.30")
    }
}


dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "LoveFinder"
include(":app")
