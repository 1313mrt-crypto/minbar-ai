package com.sokhanara.app.di

import com.sokhanara.app.ai.engine.AiEngine
import com.sokhanara.app.ai.engine.PromptBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * AI Module
 * تزریق وابستگی AI
 */
@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    
    @Provides
    @Singleton
    fun providePromptBuilder(): PromptBuilder = PromptBuilder()
    
    @Provides
    @Singleton
    fun provideAiEngine(
        promptBuilder: PromptBuilder
    ): AiEngine = AiEngine(promptBuilder)
}
