# ProGuard rules for Sokhanara App

# Preserve all public classes and members
-keepclassmembers class ** {
    *** get*();
    *** set*(...);
}

# Preserve enum values for use by valueOf reflection
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep classes used in annotations
-keep class * extends java.lang.annotation.Annotation

# Keep classes extending Parcelable
-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Hilt
-keep class dagger.hilt.** { *; }
-keep class dagger.** { *; }
-keep interface dagger.** { *; }
-keep class javax.inject.** { *; }

# Room
-keep class androidx.room.** { *; }
-keepclasseswithmembernames class * {
    @androidx.room.* <methods>;
}

# Retrofit
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Apache POI
-keep class org.apache.poi.** { *; }
-keep interface org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# iText
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Jetpack Compose
-keep @androidx.compose.runtime.Composable class ** { *; }

# Preserve line numbers
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
