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
val compose_version = "1.0.0"
val kotlin_android = "1.5.10"
val android_library = "7.1.0-alpha10"
val android_application = "7.1.0-alpha10"
