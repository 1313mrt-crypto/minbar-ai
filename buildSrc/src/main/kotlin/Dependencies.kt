object Dependencies {
    
    // Core Android
    const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
    const val lifecycleRuntimeKtx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntimeKtx}"
    const val lifecycleViewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycleRuntimeKtx}"
    
    // Coroutines
    const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    
    // Compose
    const val composeBom = "androidx.compose:compose-bom:${Versions.composeBom}"
    const val composeUi = "androidx.compose.ui:ui"
    const val composeUiGraphics = "androidx.compose.ui:ui-graphics"
    const val composeUiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
    const val composeMaterial3 = "androidx.compose.material3:material3"
    const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    const val navigationCompose = "androidx.navigation:navigation-compose:2.7.5"
    
    // Compose Debug
    const val composeUiTooling = "androidx.compose.ui:ui-tooling"
    const val composeUiTestManifest = "androidx.compose.ui:ui-test-manifest"
    
    // Hilt
    const val hiltAndroid = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hiltCompiler = "com.google.dagger:hilt-compiler:${Versions.hilt}"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:${Versions.hiltNavigation}"
    
    // Room
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    
    // DataStore
    const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
    
    // Retrofit & OkHttp
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val retrofitConverterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    const val okhttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    
    // Apache POI (PowerPoint & Word)
    const val poiOoxml = "org.apache.poi:poi-ooxml:${Versions.poi}"
    const val poi = "org.apache.poi:poi:${Versions.poi}"
    
    // iText (PDF)
    const val itext = "com.itextpdf:itextpdf:${Versions.itext}"
    
    // ONNX Runtime (Offline AI)
    const val onnxRuntime = "com.microsoft.onnxruntime:onnxruntime-android:${Versions.onnxRuntime}"
    
    // Coil (Image Loading)
    const val coil = "io.coil-kt:coil-compose:${Versions.coil}"
    
    // ExoPlayer (Media)
    const val exoplayer = "androidx.media3:media3-exoplayer:${Versions.exoplayer}"
    const val exoplayerUi = "androidx.media3:media3-ui:${Versions.exoplayer}"
    
    // Tarsos DSP (Audio Analysis)
    const val tarsosDsp = "be.tarsos.dsp:core:${Versions.tarsosDsp}"
    
    // Gson
    const val gson = "com.google.code.gson:gson:2.10.1"
    
    // Testing
    const val junit = "junit:junit:${Versions.junit}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val composeUiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val turbine = "app.cash.turbine:turbine:${Versions.turbine}"
    const val roomTesting = "androidx.room:room-testing:${Versions.room}"
}