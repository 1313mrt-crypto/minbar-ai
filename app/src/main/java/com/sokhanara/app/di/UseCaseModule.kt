package com.sokhanara.app.di

import com.sokhanara.app.domain.repository.*
import com.sokhanara.app.domain.usecase.audio.GenerateAudioUseCase
import com.sokhanara.app.domain.usecase.emotion.AnalyzeEmotionUseCase
import com.sokhanara.app.domain.usecase.export.*
import com.sokhanara.app.domain.usecase.library.*
import com.sokhanara.app.domain.usecase.source.ParseSourceUseCase
import com.sokhanara.app.domain.usecase.speech.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Use Case Module
 * تزریق وابستگی Use Case‌ها
 */
@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    
    // Speech Use Cases
    @Provides
    @ViewModelScoped
    fun provideGenerateSpeechUseCase(
        repository: SpeechRepository
    ): GenerateSpeechUseCase = GenerateSpeechUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideGetAllSpeechesUseCase(
        repository: SpeechRepository
    ): GetAllSpeechesUseCase = GetAllSpeechesUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideGetSpeechByIdUseCase(
        repository: SpeechRepository
    ): GetSpeechByIdUseCase = GetSpeechByIdUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideDeleteSpeechUseCase(
        repository: SpeechRepository
    ): DeleteSpeechUseCase = DeleteSpeechUseCase(repository)
    
    // Source Use Cases
    @Provides
    @ViewModelScoped
    fun provideParseSourceUseCase(
        repository: SourceRepository
    ): ParseSourceUseCase = ParseSourceUseCase(repository)
    
    // Export Use Cases
    @Provides
    @ViewModelScoped
    fun provideGeneratePowerPointUseCase(
        repository: ExportRepository
    ): GeneratePowerPointUseCase = GeneratePowerPointUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideGenerateInfographicUseCase(
        repository: ExportRepository
    ): GenerateInfographicUseCase = GenerateInfographicUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideGenerateChecklistUseCase(
        repository: ExportRepository
    ): GenerateChecklistUseCase = GenerateChecklistUseCase(repository)
    
    // Audio Use Cases
    @Provides
    @ViewModelScoped
    fun provideGenerateAudioUseCase(
        repository: AudioRepository
    ): GenerateAudioUseCase = GenerateAudioUseCase(repository)
    
    // Emotion Use Cases
    @Provides
    @ViewModelScoped
    fun provideAnalyzeEmotionUseCase(
        repository: EmotionRepository
    ): AnalyzeEmotionUseCase = AnalyzeEmotionUseCase(repository)
    
    // Library Use Cases
    @Provides
    @ViewModelScoped
    fun provideGetTopicsUseCase(
        repository: LibraryRepository
    ): GetTopicsUseCase = GetTopicsUseCase(repository)
    
    @Provides
    @ViewModelScoped
    fun provideSuggestSourcesUseCase(
        repository: LibraryRepository
    ): SuggestSourcesUseCase = SuggestSourcesUseCase(repository)
}
