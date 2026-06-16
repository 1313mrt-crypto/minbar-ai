package com.sokhanara.app

import android.app.Application
import com.sokhanara.app.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Application Class
 * نقطه شروع برنامه - تنظیمات اولیه و Dependency Injection
 */
@HiltAndroidApp
class SokharaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Timber برای لاگ‌گیری (فقط در Debug)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("🚀 Sokhara Application Started")
        }
        
        // تنظیمات اولیه
        initializeApp()
    }

    private fun initializeApp() {
        // می‌تونی اینجا تنظیمات اضافی بذاری
        // مثل: Analytics, Crash Reporting, etc.
        Timber.d("✅ App Initialized Successfully")
    }
}