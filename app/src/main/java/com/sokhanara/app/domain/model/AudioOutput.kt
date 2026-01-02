package com.sokhanara.app.domain.model

/**
 * خروجی صوتی
 */
data class AudioOutput(
    val filePath: String,
    val duration: Long, // به میلی‌ثانیه
    val fileSize: Long,
    val format: AudioFormat,
    val prosody: ProsodySettings
)

enum class AudioFormat {
    MP3,
    WAV,
    OGG
}

data class ProsodySettings(
    val speed: Float = 1.0f,
    val pitch: Float = 1.0f,
    val volume: Float = 1.0f,
    val pauses: Map<Int, Long> = emptyMap() // موقعیت → مدت مکث
)