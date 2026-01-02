package com.sokhanara.app.domain.model

/**
 * پروفایل کاربر
 */
data class UserProfile(
    val id: String,
    val name: String?,
    val preferredLanguage: Language = Language.PERSIAN,
    val preferredStyle: SpeechStyle = SpeechStyle.ROOZEH,
    val statistics: UserStatistics,
    val settings: UserSettings
)

data class UserStatistics(
    val totalSpeeches: Int = 0,
    val totalDuration: Long = 0, // به دقیقه
    val favoriteTopics: List<String> = emptyList(),
    val averageImpactScore: Float = 0f
)

data class UserSettings(
    val offlineMode: Boolean = false,
    val autoSave: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val defaultTheme: VisualTheme = VisualTheme.MOHARRAM
)