package com.sokhanara.app.di

import android.content.Context
import com.sokhanara.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * App Module
 * تنظیمات کلی برنامه
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ): Context = context

    @Provides
    @Singleton
    fun provideAppName(): String = Constants.APP_NAME

    @Provides
    @Singleton
    fun provideAppVersion(): String = Constants.APP_VERSION
}