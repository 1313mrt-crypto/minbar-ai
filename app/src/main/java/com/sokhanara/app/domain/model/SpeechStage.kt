package com.sokhanara.app.domain.model

/**
 * مراحل ۵گانه سخنرانی
 */
enum class SpeechStage(
    val order: Int,
    val title: String,
    val description: String
) {
    MOTIVATION(
        order = 1,
        title = "انگیزه‌سازی",
        description = "جلب توجه و ایجاد علاقه در مخاطب"
    ),
    CONVICTION(
        order = 2,
        title = "اقناع اندیشه",
        description = "ارائه دلایل منطقی و استدلال"
    ),
    EMOTION(
        order = 3,
        title = "پرورش احساس",
        description = "بیدار کردن احساسات و عواطف"
    ),
    ACTION(
        order = 4,
        title = "رفتارسازی",
        description = "راهنمایی عملی و دستورالعمل"
    ),
    RAWZEH(
        order = 5,
        title = "روضه",
        description = "اوج احساسی و معنوی"
    );
    
    companion object {
        fun getByOrder(order: Int): SpeechStage? {
            return values().find { it.order == order }
        }
    }
}