package com.sokhanara.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Emotion Analysis Entity
 * جدول تحلیل عاطفی
 */
@Entity(
    tableName = "emotion_analysis",
    foreignKeys = [
        ForeignKey(
            entity = SpeechEntity::class,
            parentColumns = ["id"],
            childColumns = ["speechId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EmotionEntity(
    @PrimaryKey
    val id: String,
    val speechId: String,
    
    // تحلیل pitch
    val averagePitch: Float,
    val pitchVariation: Float,
    val minPitch: Float,
    val maxPitch: Float,
    
    // تحلیل سرعت
    val averageSpeed: Float, // کلمه در دقیقه
    val speedVariation: Float,
    
    // امتیاز تأثیرگذاری
    val impactScore: Float, // 0-100
    
    // پیشنهادات
    val suggestions: String?,
    
    val analyzedAt: Long