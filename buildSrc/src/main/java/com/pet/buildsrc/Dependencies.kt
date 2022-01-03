package com.pet.buildsrc

object Versions {
    val compose_version = "1.0.0"
    val kotlin_android = "1.5.10"
    val android_library = "7.1.0-alpha10"
    val kotlin_jvm = "1.5.10"
    val androidx_core_ktx = "1.7.0"
    val androidx_lifecycle_runtime = "2.4.0"
    val junit = "4.13.2"
    val ext_junit = "1.1.3"
    val android_test_espresso_core = "3.4.0"
    val androidx_compose_ui_test_junit4 = "1.0.0"
    val compose_material_icons_extended = compose_version
    val activity_compose = "1.4.0"
    val constraintlayout_compose = "1.0.0-rc01"
    val socket_io_client = "2.0.1"
    val work_manager = "2.7.1"
    val google_code_gson = "2.8.5"
    val compose_navigation = "2.4.0-beta02"
    val dagger_hilt_android = "2.39.1"
    val squareup_retrofit = "2.9.0"
    val gson_converter = "2.5.0"
    val dexter = "6.2.3"
    val fragment_ktx = "1.3.0"
    val activity_ktx = "1.3.0-alpha02"
    val hilt_navigation = "1.0.0"
    val hilt_navigation_compose = "1.0.0-beta01"
    val hilt_work = "1.0.0"
    val loggingInterceptor = "3.8.0"

}

object Libs {
    val compose_ui = "androidx.compose.ui:ui:${Versions.compose_version}"
    val compose_material = "androidx.compose.material:material:${Versions.compose_version}"
    val compose_ui_tooling_preview =
        "androidx.compose.ui:ui-tooling-preview:${Versions.compose_version}"
    val androidx_core_ktx = "androidx.core:core-ktx:${Versions.androidx_core_ktx}"
    val androidx_lifecycle_runtime_ktx =
        "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.androidx_lifecycle_runtime}"
    val compose_material_icons_extended =
        "androidx.compose.material:material-icons-extended:${Versions.compose_version}"
    val compose_activity = "androidx.activity:activity-compose:${Versions.activity_compose}"
    val compose_constraintlayout =
        "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintlayout_compose}"
    val socket_io_client = "io.socket:socket.io-client:${Versions.socket_io_client}"
    val work_runtime_ktx = "androidx.work:work-runtime-ktx:${Versions.work_manager}"
    val google_code_gson = "com.google.code.gson:gson:${Versions.google_code_gson}"
    val compose_navigation = "androidx.navigation:navigation-compose:${Versions.compose_navigation}"
    val com_google_dagger_hilt = "com.google.dagger:hilt-android:${Versions.dagger_hilt_android}"
    val retrofit2 = "com.squareup.retrofit2:retrofit:${Versions.squareup_retrofit}"
    val gson_converter_factory = "com.squareup.retrofit2:converter-gson:{${Versions.gson_converter}}"
    val kapt_com_google_dagger_hilt = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt_android}"
    val dexter_permission = "com.karumi:dexter:{${Versions.dexter}}"
    val activity_ktx = "androidx.activity:activity-ktx:${Versions.activity_ktx}"
    val fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.fragment_ktx}"
    val hilt_navigation = "androidx.hilt:hilt-navigation-fragment:${Versions.hilt_navigation}"
    val hilt_navigation_compose ="androidx.hilt:hilt-navigation-compose:${Versions.hilt_navigation_compose}"
    val hilt_work = "androidx.hilt:hilt-work:${Versions.hilt_work}"
    val kapt_hilt_work = "androidx.hilt:hilt-compiler:${Versions.hilt_work}"
    val logging_interceptors = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptor}"
    val runtime_liveData_compose = "androidx.compose.runtime:runtime-livedata:${Versions.compose_version}"


}

object TestLibs {
    val junit = "androidx.test.ext:junit:${Versions.junit}"
}

object DebugLibs {
    val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose_version}"
}

object AndroidTestLibs {
    val ext_junit = "androidx.test.ext:junit:${Versions.ext_junit}"
    val android_test_espresso_core =
        "androidx.test.espresso:espresso-core:${Versions.android_test_espresso_core}"
    val androidx_compose_ui_test_junit4 =
        "androidx.compose.ui:ui-test-junit4:${Versions.androidx_compose_ui_test_junit4}"
}