object ApplicationId {
    const val id = "com.rnc.ns.sessionmanager"
}

object Modules {
    const val presentation = "::presentation"
    const val data = "::data"
    const val domain = "::domain"
}

object Releases {
    const val versionCode = 1
    const val versionName = "1.0"
}

object Versions {
    const val minSdk = 21
    const val compileSdk = 31
    const val targetSdk = 31
    const val flow = "1.5.2"
    const val room = "2.4.2"
    const val hilt = "2.42"
    const val hiltCompiler = "1.0.0"
    const val hiltWork = "1.0.0"
    const val work = "2.7.1"
    const val lifecycle = "2.4.1"
    const val navigation = "2.4.2"
    const val coreKtx = "1.7.0"
    const val compose = "1.1.1"
    const val composeActivity = "1.4.0"
    const val fragment = "1.4.1"
    const val junit = "4.13.2"
    const val junitExt = "1.1.3"
    const val espressoCore = "3.4.0"
}

object Libraries {
    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${Versions.hilt}"
    const val hiltCompiler = "androidx.hilt:hilt-compiler:${Versions.hiltCompiler}"
    const val hiltWork = "androidx.hilt:hilt-work:${Versions.hiltWork}"

    const val flow = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.flow}"

    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
}

object AndroidLibraries {
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"

    const val workRuntime = "androidx.work:work-runtime-ktx:${Versions.work}"

    const val lifecycleViewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"

    const val navigationFragment = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
    const val navigationUI = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
    const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigation}"

    const val composeUI = "androidx.compose.ui:ui:${Versions.compose}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.compose}"
    const val composePreview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose}"
    const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivity}"

    const val fragment = "androidx.fragment:fragment:${Versions.fragment}"
}

object TestLibraries {
    const val junit = "junit:junit:${Versions.junit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCore}"
    const val junitCompose = "androidx.compose.ui:ui-test-junit4:${Versions.compose}"
    const val compostPreview = "androidx.compose.ui:ui-tooling:${Versions.compose}"
}