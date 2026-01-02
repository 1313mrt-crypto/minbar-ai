package com.sokhanara.app.domain.model

/**
 * موضوعات (کتابخانه)
 */
data class Topic(
    val id: String,
    val title: String,
    val titleArabic: String?,
    val category: TopicCategory,
    val description: String?,
    val sources: List<String>,
    val keywords: List<String>,
    val usageCount: Int = 0
)

enum class TopicCategory(val displayName: String) {
    QURAN("قرآن"),
    HADITH("حدیث"),
    IMAM("امام"),
    AKHLAQ("اخلاق"),
    HISTORY("تاریخ"),
    OCCASION("مناسبت");
}