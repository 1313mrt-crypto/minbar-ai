package com.sokhanara.app.domain.model

/**
 * زبان‌های پشتیبانی‌شده
 */
enum class Language(val code: String, val displayName: String) {
    PERSIAN("fa", "فارسی"),
    ARABIC("ar", "عربی");
    
    companion object {
        fun fromCode(code: String): Language {
            return values().find { it.code == code } ?: PERSIAN
        }
    }
}