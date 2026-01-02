package com.sokhanara.app.domain.model

/**
 * مدل سخنرانی
 */
data class Speech(
    val id: String,
    val title: String,
    val topic: String?,
    val language: Language,
    val style: SpeechStyle,
    val theme: VisualTheme,
    
    // محتوای ۵ مرحله
    val stages: Map<SpeechStage, String>,
    
    // منابع
    val sources: List<Source>,
    
    // خروجی‌ها
    val outputs: SpeechOutputs,
    
    // متادیتا
    val metadata: SpeechMetadata
)

data class SpeechOutputs(
    val pdfPath: String? = null,
    val pptxPath: String? = null,
    val infographicPath: String? = null,
    val audioPath: String? = null,
    val checklistPath: String? = null
)

data class SpeechMetadata(
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
    val estimatedDuration: Int? = null, // به دقیقه
    val viewCount: Int = 0
)