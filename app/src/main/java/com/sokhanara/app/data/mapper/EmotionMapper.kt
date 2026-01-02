package com.sokhanara.app.data.mapper

import com.sokhanara.app.data.local.entity.EmotionEntity
import com.sokhanara.app.domain.model.*

/**
 * تبدیل Emotion Entity ↔ Domain
 */
object EmotionMapper {
    
    fun toEntity(emotion: EmotionAnalysis): EmotionEntity {
        return EmotionEntity(
            id = emotion.id,
            speechId = emotion.speechId,
            averagePitch = emotion.pitchAnalysis.averagePitch,
            pitchVariation = emotion.pitchAnalysis.pitchVariation,
            minPitch = emotion.pitchAnalysis.minPitch,
            maxPitch = emotion.pitchAnalysis.maxPitch,
            averageSpeed = emotion.speedAnalysis.averageSpeed,
            speedVariation = emotion.speedAnalysis.speedVariation,
            impactScore = emotion.impactScore,
            suggestions = emotion.suggestions.joinToString("\n"),
            analyzedAt = emotion.analyzedAt
        )
    }
    
    fun toDomain(entity: EmotionEntity): EmotionAnalysis {
        return EmotionAnalysis(
            id = entity.id,
            speechId = entity.speechId,
            pitchAnalysis = PitchAnalysis(
                averagePitch = entity.averagePitch,
                pitchVariation = entity.pitchVariation,
                minPitch = entity.minPitch,
                maxPitch = entity.maxPitch,
                pitchRange = entity.maxPitch - entity.minPitch
            ),
            speedAnalysis = SpeedAnalysis(
                averageSpeed = entity.averageSpeed,
                speedVariation = entity.speedVariation,
                minSpeed = entity.averageSpeed - entity.speedVariation,
                maxSpeed = entity.averageSpeed + entity.speedVariation
            ),
            impactScore = entity.impactScore,
            suggestions = entity.suggestions?.split("\n") ?: emptyList(),
            analyzedAt = entity.analyzedAt
        )
    }
    
    fun toDomainList(entities: List<EmotionEntity>): List<EmotionAnalysis> {
        return entities.map { toDomain(it) }
    }
}