import com.pet.buildsrc.Versions.dagger_hilt_android

// Top-level build file where you can add configuration options common to all sub-projects/modules.
tasks{
    val clean by registering(Delete::class){
        delete(buildDir)
    }
}

buildscript {
    repositories {
        // other repositories...
        mavenCentral()

        dependencies {
            // other plugins...
            classpath("com.google.dagger:hilt-android-gradle-plugin:2.40.5")
        }


    }
    

    
}





