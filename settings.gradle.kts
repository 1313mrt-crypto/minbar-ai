```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        
        // برای کتابخانه‌های خاص
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "Sokhanara"
include(":app")