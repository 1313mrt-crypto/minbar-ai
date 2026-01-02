package com.sokhanara.app.di

import com.sokhanara.app.data.repository.*
import com.sokhanara.app.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository Module
 * تزریق وابستگی Repository‌ها
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindSpeechRepository(
        impl: SpeechRepositoryImpl
    ): SpeechRepository
    
    @Binds
    @Singleton
    abstract fun bindLibraryRepository(
        impl: LibraryRepositoryImpl
    ): LibraryRepository
    
    @Binds
    @Singleton
    abstract fun bindEmotionRepository(
        impl: EmotionRepositoryImpl
    ): EmotionRepository
}