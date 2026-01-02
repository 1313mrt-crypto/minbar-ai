package com.sokhanara.app.domain.usecase.speech

import com.sokhanara.app.domain.repository.SpeechRepository
import javax.inject.Inject

/**
 * Use Case: حذف سخنرانی
 */
class DeleteSpeechUseCase @Inject constructor(
    private val speechRepository: SpeechRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return try {
            speechRepository.deleteSpeech(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}