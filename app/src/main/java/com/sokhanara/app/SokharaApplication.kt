package com.sokhanara.app

import android.app.Application
import android.os.StrictMode
import com.sokhanara.app.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader

@HiltAndroidApp
class SokharaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("🚀 Sokhara Application Started")

            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }

        // initialize heavy libraries in background
        CoroutineScope(Dispatchers.Default).launch {
            try {
                PDFBoxResourceLoader.init(applicationContext)
                Timber.d("PDFBox initialized in background")
            } catch (t: Throwable) {
                Timber.e(t, "PDFBox background init failed")
            }
        }
    }
}
