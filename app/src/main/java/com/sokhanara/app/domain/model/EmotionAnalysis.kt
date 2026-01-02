package com.sokhanara.app.domain.model

/**
 * تحلیل عاطفی صدا
 */
data class EmotionAnalysis(
    val id: String,
    val speechId: String,
    
    // تحلیل pitch (زیر و بمی صدا)
    val pitchAnalysis: PitchAnalysis,
    
    // تحلیل سرعت
    val speedAnalysis: SpeedAnalysis,
    
    // امتیاز کلی
    val impactScore: Float, // 0-100
    
    // پیشنهادات
    val suggestions: List<String>,
    
    val analyzedAt: Long
)

data class PitchAnalysis(
    val averagePitch: Float,
    val pitchVariation: Float,
    val minPitch: Float,
    val maxPitch: Float,
    val pitchRange: Float
) {
    val quality: AnalysisQuality
        get() = when {
            pitchVariation > 50 -> AnalysisQuality.EXCELLENT
            pitchVariation > 30 -> AnalysisQuality.GOOD
            pitchVariation > 15 -> AnalysisQuality.FAIR
            else -> AnalysisQuality.POOR
        }
}

data class SpeedAnalysis(
    val averageSpeed: Float, // کلمه در دقیقه
    val speedVariation: Float,
    val minSpeed: Float,
    val maxSpeed: Float
) {
    val quality: AnalysisQuality
        get() = when {
            averageSpeed in 120f..180f -> AnalysisQuality.EXCELLENT
            averageSpeed in 100f..200f -> AnalysisQuality.GOOD
            averageSpeed in 80f..220f -> AnalysisQuality.FAIR
            else -> AnalysisQuality.POOR
        }
}

enum class AnalysisQuality {
    EXCELLENT,
    GOOD,
    FAIR,
    POOR
}