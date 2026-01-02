package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Rawzeh Template - مرحله ۵: روضه
 */
class RawzehTemplate : BaseTemplate() {
    
    override val stage = SpeechStage.RAWZEH
    
    override fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String {
        return """
            شما یک سخنران حرفه‌ای هستید. وظیفه شما نوشتن **مرحله پنجم: روضه** است.
            
            موضوع: $topic
            
            منابع معتبر:
            $sourceContent
            
            سبک سخنرانی:
            ${getStyleGuidelines(style)}
            
            هدف مرحله روضه:
            - رسیدن به اوج احساسی
            - ایجاد حس معنوی عمیق
            - اشک‌آوری و تأثیرگذاری نهایی
            - پایان‌بندی قوی و ماندگار
            
            دستورالعمل‌ها:
            1. با **تصویرسازی بسیار قوی** و **احساسی** بنویسید
            2. از **تکرار عبارات کلیدی** برای تأکید استفاده کنید
            3. لحن را **بسیار عاطفی** و **نوحه‌وار** کنید
            4. به مخاطب **مستقیم** خطاب کنید (تو، شما)
            5. با **دعا** یا **مناجات** به پایان برسانید
            6. طول: 150-250 کلمه
            
            تکنیک‌های ویژه روضه:
            - توصیف صحنه‌های دردناک
            - استفاده از ندا و خطاب مستقیم
            - تکرار برای تأکید
            - جملات کوتاه و پرتأثیر
            - موسیقی کلام (ریتم و آهنگ)
            
            محدودیت‌ها:
            - فقط از منابع داده‌شده استفاده کنید
            - افراط در تراژدی نکنید
            - زبان: فارسی بسیار ادبی و احساسی
            
            لطفاً فقط متن مرحله روضه را بنویسید:
        """.trimIndent()
    }
}