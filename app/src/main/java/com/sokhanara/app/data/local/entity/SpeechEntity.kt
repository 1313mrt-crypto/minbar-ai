package com.sokhanara.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sokhanara.app.data.local.database.Converters

/**
 * Speech Entity
 * جدول سخنرانی‌ها
 */
@Entity(tableName = "speeches")
@TypeConverters(Converters::class)
data class SpeechEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val topic: String?,
    val language: String, // fa, ar
    val style: String, // roozeh, motivational, educational
    val theme: String, // moharram, ramadan, eid, academic
    
    // محتوای ۵ مرحله
    val stageMotivation: String?,
    val stageConviction: String?,
    val stageEmotion: String?,
    val stageAction: String?,
    val stageRawzeh: String?,
    
    // منابع
    val sources: List<String>,
    
    // خروجی‌ها
    val pdfPath: String?,
    val pptxPath: String?,
    val infographicPath: String?,
    val audioPath: String?,
    val checklistPath: String?,
    
    // متادیتا
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
    val estimatedDuration: Int? // به دقیقه
)