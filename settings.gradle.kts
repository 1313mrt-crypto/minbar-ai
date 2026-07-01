pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    plugins {
        id("com.android.application") version "8.4.0"
        id("org.jetbrains.kotlin.android") version "1.9.24"
        id("com.google.dagger.hilt.android") version "2.48"
        id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")  // ← اضافه کنید
    }
}

rootProject.name = "minbar-ai"

include(":app")
