package com.sokhanara.app.ai.template

import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.SpeechStyle

/**
 * Base Template
 * کلاس پایه برای تمام template‌ها
 */
abstract class BaseTemplate {
    
    abstract val stage: SpeechStage
    
    abstract fun buildPrompt(
        topic: String,
        sourceContent: String,
        style: SpeechStyle,
        language: String
    ): String
    
    protected fun getStyleGuidelines(style: SpeechStyle): String {
        return when (style) {
            SpeechStyle.ROOZEH -> """
                - لحن عاطفی و احساسی
                - استفاده از تشبیهات و تصویرسازی
                - ایجاد حس همدردی با مخاطب
                - پایان‌بندی قوی و تأثیرگذار
            """.trimIndent()
            
            SpeechStyle.MOTIVATIONAL -> """
                - لحن انگیزشی و پرانرژی
                - تمرکز بر توانمندی‌ها و امید
                - استفاده از داستان‌های موفقیت
                - تشویق به عمل
            """.trimIndent()
            
            SpeechStyle.EDUCATIONAL -> """
                - لحن آموزشی و واضح
                - ساختار منطقی و گام‌به‌گام
                - استفاده از مثال‌های کاربردی
                - خلاصه‌سازی نکات کلیدی
            """.trimIndent()
            
            SpeechStyle.TRADITIONAL -> """
                - لحن سنتی و محترمانه
                - استفاده از ادبیات کلاسیک
                - رعایت آداب سخنرانی
                - احترام به مخاطب
            """.trimIndent()
        }
    }
}