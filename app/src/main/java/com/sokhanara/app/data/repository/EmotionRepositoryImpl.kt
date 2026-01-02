package com.sokhanara.app.data.repository

import com.sokhanara.app.data.local.dao.EmotionDao
import com.sokhanara.app.data.mapper.EmotionMapper
import com.sokhanara.app.domain.model.*
import com.sokhanara.app.domain.repository.EmotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * پیاده‌سازی Emotion Repository
 */
class EmotionRepositoryImpl @Inject constructor(
    private val emotionDao: EmotionDao
) : EmotionRepository {
    
    override suspend fun analyzeEmotion(
        speechId: String,
        audioPath: String
    ): Result<EmotionAnalysis> {
        return try {
            // TODO: اینجا باید تحلیل واقعی صدا انجام بشه
            // فعلاً یک نمونه ساده برمی‌گردونیم
            
            val analysis = EmotionAnalysis(
                id = "emotion_${System.currentTimeMillis()}",
                speechId = speechId,
                pitchAnalysis = PitchAnalysis(
                    averagePitch = 150f,
                    pitchVariation = 40f,
                    minPitch = 120f,
                    maxPitch = 200f,
                    pitchRange = 80f
                ),
                speedAnalysis = SpeedAnalysis(
                    averageSpeed = 140f,
                    speedVariation = 20f,
                    minSpeed = 120f,
                    maxSpeed = 160f
                ),
                impactScore = 75f,
                suggestions = listOf(
                    "در بخش‌های احساسی بیشتر مکث کنید",
                    "فراز و فرود صدا خوب است",
                    "سرعت مناسب است"
                ),
                analyzedAt = System.currentTimeMillis()
            )
            
            saveEmotionAnalysis(analysis)
            Result.success(analysis)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override fun getEmotionsBySpeech(speechId: String): Flow<List<EmotionAnalysis>> {
        return emotionDao.getEmotionsBySpeech(speechId)
            .map { entities -> EmotionMapper.toDomainList(entities) }
    }
    
    override suspend fun saveEmotionAnalysis(analysis: EmotionAnalysis) {
        val entity = EmotionMapper.toEntity(analysis)
        emotionDao.insertEmotion(entity)
    }
}