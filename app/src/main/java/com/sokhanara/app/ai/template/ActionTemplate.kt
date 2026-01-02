package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Action Template - مرحله ۴: رفتارسازی
 */
class ActionTemplate : BaseTemplate() {
    
    override val stage = SpeechStage.ACTION
    
    override fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن **مرحله چهارم: رفتارسازی** است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک سخنرانی:
            ${getStyleGuidelines(style)}
            
            هدف مرحله رفتارسازی:
            - راهنمایی عملی و کاربردی
            - تبدیل احساس به عمل
            - ارائه گام‌های مشخص
            - انگیزه‌دهی برای شروع
            
            دستورالعمل‌ها:
            1. حداقل 3-5 **اقدام عملی** و **قابل اجرا** ارائه دهید
            2. هر اقدام را واضح و ساده توضیح دهید
            3. از **قالب گام‌به‌گام** استفاده کنید
            4. با عبارات تشویقی و امیدوارکننده بنویسید
            5. طول: 200-300 کلمه
            
            ساختار پیشنهادی:
            - مقدمه کوتاه (چرا باید عمل کنیم؟)
            - اقدام اول: ...
            - اقدام دوم: ...
            - اقدام سوم: ...
            - جمله انگیزشی پایانی
            
            محدودیت‌ها:
            - فقط از منابع داده‌شده استفاده کنید
            - راهکارها باید واقعی و قابل اجرا باشند
            - زبان: فارسی ساده و مستقیم
            
            لطفاً فقط متن مرحله رفتارسازی را بنویسید:
        """.trimIndent()
    }
}