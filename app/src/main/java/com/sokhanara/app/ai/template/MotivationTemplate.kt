package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Motivation Template - مرحله ۱: انگیزه‌سازی
 */
class MotivationTemplate : BaseTemplate() {
    
    override val stage = SpeechStage.MOTIVATION
    
    override fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن **مرحله اول: انگیزه‌سازی** است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک سخنرانی:
            ${getStyleGuidelines(style)}
            
            هدف مرحله انگیزه‌سازی:
            - جلب توجه مخاطب در 30 ثانیه اول
            - ایجاد کنجکاوی و علاقه
            - ارتباط عاطفی با مخاطب
            - طرح سوال یا موقعیت چالش‌برانگیز
            
            دستورالعمل‌ها:
            1. با یک **داستان کوتاه** (2-3 جمله)، **آمار جالب** یا **سوال تأثیرگذار** شروع کنید
            2. به موضوع اصلی ربط دهید
            3. توضیح دهید چرا این موضوع برای مخاطب مهم است
            4. احساسات مخاطب را درگیر کنید
            5. طول: 150-250 کلمه
            
            محدودیت‌ها:
            - فقط از منابع داده‌شده استفاده کنید
            - هیچ اطلاعات جعلی اضافه نکنید
            - زبان: فارسی روان و ساده
            
            لطفاً فقط متن مرحله انگیزه‌سازی را بنویسید (بدون عنوان یا توضیحات اضافی):
        """.trimIndent()
    }
}