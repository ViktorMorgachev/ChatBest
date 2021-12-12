package com.pet.buildsrc

object Versions {
    val compose_version = "1.0.0"
    val kotlin_android = "1.5.10"
    val android_library = "7.1.0-alpha10"
    val android_application = "7.1.0-alpha10"
    val kotlin_jvm = "1.5.30"
    val androidx_core_ktx = "1.7.0"
    val androidx_lifecycle_runtime = "2.3.1"
    val android_activity_compose = "1.3.0"
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