package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Emotion Template - مرحله ۳: پرورش احساس
 */
class EmotionTemplate : BaseTemplate() {
    
    override val stage = SpeechStage.EMOTION
    
    override fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن **مرحله سوم: پرورش احساس** است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک سخنرانی:
            ${getStyleGuidelines(style)}
            
            هدف مرحله پرورش احساس:
            - بیدار کردن احساسات مخاطب
            - ایجاد همدلی و همدردی
            - تصویرسازی قوی و تأثیرگذار
            - انتقال از ذهن به قلب
            
            دستورالعمل‌ها:
            1. از **تصویرسازی** و **توصیف دقیق** استفاده کنید
            2. داستان‌های احساسی و تأثیرگذار بیاورید
            3. از استعاره‌ها و تشبیهات زیبا استفاده کنید
            4. لحن را احساسی‌تر کنید (بدون افراط)
            5. طول: 200-350 کلمه
            
            تکنیک‌های پیشنهادی:
            - داستان شخصی یا واقعی
            - توصیف صحنه‌های احساسی
            - استفاده از کلمات عاطفی
            - ایجاد همذات‌پنداری
            
            محدودیت‌ها:
            - فقط از منابع داده‌شده استفاده کنید
            - افراط در احساسات نکنید
            - زبان: فارسی ادبی و زیبا
            
            لطفاً فقط متن مرحله پرورش احساس را بنویسید:
        """.trimIndent()
    }