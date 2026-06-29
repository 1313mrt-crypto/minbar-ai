plugins {
    id("com.android.application")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.sokhanara.app"
    compileSdk = Versions.compileSdk
    sourceSets {
        getByName("main") {
            manifest.srcFile("src/main/AndroidManifest.xml")
            java.directories += "src/main/java"
            res.directories += "src/main/res"
        }
    }

    defaultConfig {
        applicationId = "com.sokhanara.app"
        minSdk = Versions.minSdk
        targetSdk = Versions.targetSdk
        versionCode = Versions.versionCode
        versionName = Versions.versionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    // برای فونت‌های فارسی (جایگزین resourceConfigurations منسوخ‌شده)
    androidResources {
        localeFilters += listOf("fa", "ar", "en")
    }

    signingConfigs {
        create("release") {
            storeFile = file("${rootProject.projectDir}/app/keystore/minbar-ai-release.jks")
            storePassword = SigningConfig.storePassword
            keyAlias = SigningConfig.keyAlias
            keyPassword = SigningConfig.keyPassword
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            // برای رفع تداخل Apache POI
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    // Core
    implementation(Dependencies.coreKtx)
    implementation(Dependencies.lifecycleRuntimeKtx)
    implementation(Dependencies.lifecycleViewModelCompose)

    // Coroutines
    implementation(Dependencies.coroutinesCore)
    implementation(Dependencies.coroutinesAndroid)

    // Compose
    implementation(platform(Dependencies.composeBom))
    implementation(Dependencies.composeUi)
    implementation(Dependencies.composeUiGraphics)
    implementation(Dependencies.composeUiToolingPreview)
    implementation(Dependencies.composeMaterial3)
    implementation(Dependencies.activityCompose)
    implementation(Dependencies.navigationCompose)

    // Hilt
    implementation(Dependencies.hiltAndroid)
    ksp(Dependencies.hiltCompiler)
    implementation(Dependencies.hiltNavigationCompose)

    // Room
    implementation(Dependencies.roomRuntime)
    implementation(Dependencies.roomKtx)
    ksp(Dependencies.roomCompiler)

    // DataStore
    implementation(Dependencies.dataStore)

    // Network
    implementation(Dependencies.retrofit)
    implementation(Dependencies.retrofitConverterGson)
    implementation(Dependencies.okhttp)
    implementation(Dependencies.okhttpLoggingInterceptor)

    // Apache POI (PowerPoint & Word)
    implementation(Dependencies.poiOoxml)
    implementation(Dependencies.poi)

    // PDFBox (PDF Parsing - Android compatible)
    implementation(Dependencies.pdfboxAndroid)

    // ONNX Runtime (Offline AI)
    implementation(Dependencies.onnxRuntime)

    // Coil (Image Loading)
    implementation(Dependencies.coil)

    // ExoPlayer (Media)
    implementation(Dependencies.exoplayer)
    implementation(Dependencies.exoplayerUi)

    // Tarsos DSP (Audio Analysis)
    //implementation("com.github.tarsos:TarsosDSP:2.5")

    // Gson
    implementation(Dependencies.gson)

    // Timber (Logging)
    implementation("com.jakewharton.timber:timber:5.0.1")

    // Debug
    debugImplementation(Dependencies.composeUiTooling)
    debugImplementation(Dependencies.composeUiTestManifest)

    // Testing
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.mockk)
    testImplementation(Dependencies.coroutinesTest)
    testImplementation(Dependencies.turbine)
    testImplementation(Dependencies.roomTesting)

    androidTestImplementation(Dependencies.junitExt)
    androidTestImplementation(Dependencies.espresso)
    androidTestImplementation(platform(Dependencies.composeBom))
    androidTestImplementation(Dependencies.composeUiTestJunit4)
}
