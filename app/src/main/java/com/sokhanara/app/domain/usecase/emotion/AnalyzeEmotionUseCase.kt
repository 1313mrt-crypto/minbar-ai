package com.sokhanara.app.domain.usecase.emotion

import com.sokhanara.app.domain.model.EmotionAnalysis
import com.sokhanara.app.domain.repository.EmotionRepository
import javax.inject.Inject

/**
 * Use Case: تحلیل عاطفی صدا
 */
class AnalyzeEmotionUseCase @Inject constructor(
    private val emotionRepository: EmotionRepository
) {
    suspend operator fun invoke(
        speechId: String,
        audioPath: String
    ): Result<EmotionAnalysis> {
        return emotionRepository.analyzeEmotion(speechId, audioPath)
    }
}