package com.sokhanara.app.ai.engine

import com.sokhanara.app.ai.template.*
import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle
import javax.inject.Inject

/**
 * Prompt Builder
 * سازنده پرامپت‌های هوشمند برای AI
 */
class PromptBuilder @Inject constructor() {
    
    private val templates = mapOf(
        SpeechStage.MOTIVATION to MotivationTemplate(),
        SpeechStage.CONVICTION to ConvictionTemplate(),
        SpeechStage.EMOTION to EmotionTemplate(),
        SpeechStage.ACTION to ActionTemplate(),
        SpeechStage.RAWZEH to RawzehTemplate()
    )
    
    fun buildPromptForStage(
        stage: SpeechStage,
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String = "fa"
    ): String {
        val template = templates[stage] 
            ?: throw IllegalArgumentException("Template not found for stage: $stage")
        
        return template.buildPrompt(
            topic = topic,
            sourceContent = sourceContent,
            style = style,
            language = language
        )
    }
    
    fun buildFullSpeechPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String = "fa"
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن یک سخنرانی کامل ۵ مرحله‌ای است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک: ${style.displayName}
            
            ساختار سخنرانی:
            1. انگیزه‌سازی (150-250 کلمه)
            2. اقناع اندیشه (250-400 کلمه)
            3. پرورش احساس (200-350 کلمه)
            4. رفتارسازی (200-300 کلمه)
            5. روضه (150-250 کلمه)
            
            دستورالعمل کلی:
            - فقط از منابع داده‌شده استفاده کنید
            - هیچ اطلاعات جعلی اضافه نکنید
            - زبان: فارسی روان و ادبی
            - محتوا باید منسجم و پیوسته باشد
            
            لطفاً سخنرانی کامل را با جداسازی واضح هر مرحله بنویسید:
        """.trimIndent()
    }
}