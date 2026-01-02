package com.sokhanara.app.domain.usecase.speech

import com.sokhanara.app.domain.model.*
import com.sokhanara.app.domain.repository.SpeechRepository
import javax.inject.Inject

/**
 * Use Case: تولید سخنرانی
 */
class GenerateSpeechUseCase @Inject constructor(
    private val speechRepository: SpeechRepository
) {
    suspend operator fun invoke(
        title: String,
        topic: String?,
        sources: List<Source>,
        language: Language,
        style: SpeechStyle,
        theme: VisualTheme
    ): Result<Speech> {
        return try {
            // 1. اعتبارسنجی ورودی‌ها
            if (title.isBlank()) {
                return Result.failure(IllegalArgumentException("عنوان نمی‌تواند خالی باشد"))
            }
            
            if (sources.isEmpty()) {
                return Result.failure(IllegalArgumentException("حداقل یک منبع لازم است"))
            }
            
            // 2. تولید ID یکتا
            val speechId = generateId()
            
            // 3. ایجاد سخنرانی اولیه
            val speech = Speech(
                id = speechId,
                title = title,
                topic = topic,
                language = language,
                style = style,
                theme = theme,
                stages = emptyMap(), // بعداً توسط AI پر می‌شود
                sources = sources,
                outputs = SpeechOutputs(),
                metadata = SpeechMetadata(
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
            )
            
            // 4. ذخیره در دیتابیس
            speechRepository.saveSpeech(speech)
            
            Result.success(speech)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun generateId(): String {
        return "speech_${System.currentTimeMillis()}_${(1000..9999).random()}"
    }
}