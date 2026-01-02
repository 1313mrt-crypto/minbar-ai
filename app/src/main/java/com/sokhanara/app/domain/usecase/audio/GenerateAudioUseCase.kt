package com.sokhanara.app.domain.usecase.audio

import com.sokhanara.app.domain.model.AudioOutput
import com.sokhanara.app.domain.model.ProsodySettings
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.AudioRepository
import javax.inject.Inject

/**
 * Use Case: تولید صدا
 */
class GenerateAudioUseCase @Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(
        speech: Speech,
        prosody: ProsodySettings = ProsodySettings()
    ): Result<AudioOutput> {
        
        // ترکیب محتوای ۵ مرحله
        val fullText = speech.stages.entries
            .sortedBy { it.key.order }
            .joinToString("\n\n") { it.value }
        
        return audioRepository.generateAudio(
            text = fullText,
            language = speech.language.code,
            prosody = prosody
        )
    }
}