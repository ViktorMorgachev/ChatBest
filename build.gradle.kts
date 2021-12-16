import com.pet.buildsrc.Versions.android_library
import com.pet.buildsrc.Versions.kotlin_android

// Top-level build file where you can add configuration options common to all sub-projects/modules.
tasks{
    val clean by registering(Delete::class){
        delete(buildDir)
    }
}

buildscript {
    repositories {
        mavenCentral()
        google()
        jcenter()
    }

    dependencies {
        // other plugins...
      //  classpath("com.google.dagger:hilt-android-gradle-plugin:2.38.1")
        classpath("com.android.tools.build:gradle:7.1.0-alpha10")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.30")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
    }
}





