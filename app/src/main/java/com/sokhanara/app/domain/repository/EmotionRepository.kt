package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.EmotionAnalysis
import kotlinx.coroutines.flow.Flow

/**
 * Emotion Repository Interface
 */
interface EmotionRepository {
    
    suspend fun analyzeEmotion(
        speechId: String,
        audioPath: String
    ): Result<EmotionAnalysis>
    
    fun getEmotionsBySpeech(speechId: String): Flow<List<EmotionAnalysis>>
    
    suspend fun saveEmotionAnalysis(analysis: EmotionAnalysis)
}