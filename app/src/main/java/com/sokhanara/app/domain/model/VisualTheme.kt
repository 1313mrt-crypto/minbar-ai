package com.sokhanara.app.domain.model

/**
 * تم‌های بصری
 */
enum class VisualTheme(val code: String, val displayName: String) {
    MOHARRAM("moharram", "محرم"),
    RAMADAN("ramadan", "رمضان"),
    EID("eid", "عید"),
    ACADEMIC("academic", "آکادمیک"),
    MINIMAL("minimal", "مینیمال");
    
    companion object {
        fun fromCode(code: String): VisualTheme {
            return values().find { it.code == code } ?: MOHARRAM
        }
    }
}