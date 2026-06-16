package com.sokhanara.app.di

import android.content.Context
import com.sokhanara.app.data.local.parser.*
import com.sokhanara.app.services.export.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Parser Module
 * تزریق وابستگی پارسرها
 */
@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    
    @Provides
    @Singleton
    fun providePdfParser(
        @ApplicationContext context: Context
    ): PdfParser = PdfParser(context)
    
    @Provides
    @Singleton
    fun provideDocxParser(
        @ApplicationContext context: Context
    ): DocxParser = DocxParser(context)
    
    @Provides
    @Singleton
    fun provideTxtParser(
        @ApplicationContext context: Context
    ): TxtParser = TxtParser(context)
    
    @Provides
    @Singleton
    fun provideWebParser(): WebParser = WebParser()
}