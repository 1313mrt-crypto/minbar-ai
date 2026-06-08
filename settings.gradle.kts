pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    
    plugins {
        id("com.android.application") version "8.2.0"
        id("org.jetbrains.kotlin.android") version "1.9.20"
        id("com.google.dagger.hilt.android") version "2.50"
        id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://maven.tarsos.xyz/repository/tarsos/")
    }
}

rootProject.name = "minbar-ai"

include(":app")
