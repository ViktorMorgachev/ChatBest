import com.pet.buildsrc.Versions.android_library
import com.pet.buildsrc.Versions.kotlin_android

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        mavenCentral()
        google()
        maven {
            setUrl("https://repo1.maven.org/maven2/")
            setUrl("https://mvnrepository.com")
        }
        jcenter()

    }

    dependencies {
        // other plugins...
        classpath("com.android.tools.build:gradle:7.1.0-alpha10")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.10")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.39.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    // Warning: this repository is going to shut down soon
    }
}

tasks{
    val clean by registering(Delete::class){
        delete(buildDir)
    }
}





