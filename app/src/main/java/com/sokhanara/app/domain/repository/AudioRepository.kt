package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.AudioOutput
import com.sokhanara.app.domain.model.ProsodySettings

/**
 * Audio Repository Interface
 */
interface AudioRepository {
    
    suspend fun generateAudio(
        text: String,
        language: String,
        prosody: ProsodySettings
    ): Result<AudioOutput>
    
    suspend fun analyzeAudio(
        audioPath: String
    ): Result<Map<String, Float>>
}