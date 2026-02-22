package com.sokhanara.app.di

import android.content.Context
import com.sokhanara.app.services.export.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Service Module
 * تزریق وابستگی سرویس‌ها
 */
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    
    @Provides
    @Singleton
    fun providePdfExporter(
        @ApplicationContext context: Context
    ): PdfExporter = PdfExporter(context)
    
    @Provides
    @Singleton
    fun providePowerPointExporter(
        @ApplicationContext context: Context
    ): PowerPointExporter = PowerPointExporter(context)
    
    @Provides
    @Singleton
    fun provideInfographicExporter(
        @ApplicationContext context: Context
    ): InfographicExporter = InfographicExporter(context)
    
    @Provides
    @Singleton
    fun provideChecklistGenerator(
        @ApplicationContext context: Context
    ): ChecklistGenerator = ChecklistGenerator(context)
}
