package com.sokhanara.app.domain.model

/**
 * سبک‌های سخنرانی
 */
enum class SpeechStyle(val code: String, val displayName: String) {
    ROOZEH("roozeh", "روضه‌خوانی"),
    MOTIVATIONAL("motivational", "انگیزشی"),
    EDUCATIONAL("educational", "آموزشی"),
    TRADITIONAL("traditional", "سنتی");
    
    companion object {
        fun fromCode(code: String): SpeechStyle {
            return values().find { it.code == code } ?: ROOZEH
        }
    }
}