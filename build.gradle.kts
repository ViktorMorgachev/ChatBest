// Top-level build file where you can add configuration options common to all sub-projects/modules.
tasks{
    val clean by registering(Delete::class){
        delete(buildDir)
    }
}

buildscript {
   /* ext {

    }*/
}