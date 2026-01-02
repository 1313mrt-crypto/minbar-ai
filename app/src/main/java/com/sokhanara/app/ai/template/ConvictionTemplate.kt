package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Conviction Template - مرحله ۲: اقناع اندیشه
 */
class ConvictionTemplate : BaseTemplate() {
    
    override val stage = SpeechStage.CONVICTION
    
    override fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن **مرحله دوم: اقناع اندیشه** است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک سخنرانی:
            ${getStyleGuidelines(style)}
            
            هدف مرحله اقناع اندیشه:
            - ارائه دلایل منطقی و محکم
            - استدلال عقلانی و استناد به منابع معتبر
            - پاسخ به شبهات احتمالی
            - متقاعدسازی ذهن مخاطب
            
            دستورالعمل‌ها:
            1. حداقل 3 دلیل قوی و منطقی ارائه دهید
            2. از آیات قرآن، احادیث یا منابع معتبر استناد کنید
            3. استدلال‌ها را با مثال‌های واقعی تقویت کنید
            4. به ترتیب از قوی‌ترین به ضعیف‌ترین دلیل پیش بروید
            5. طول: 250-400 کلمه
            
            ساختار پیشنهادی:
            - دلیل اول + شاهد
            - دلیل دوم + شاهد
            - دلیل سوم + شاهد
            - جمع‌بندی منطقی
            
            محدودیت‌ها:
            - فقط از منابع داده‌شده استفاده کنید
            - ارجاعات دقیق به منابع (مثلاً: سوره، آیه، حدیث)
            - زبان: فارسی روان و ساده
            
            لطفاً فقط متن مرحله اقناع اندیشه را بنویسید:
        """.trimIndent()
    }
}