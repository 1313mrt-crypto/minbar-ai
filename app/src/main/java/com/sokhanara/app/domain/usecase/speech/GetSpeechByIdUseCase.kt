package com.sokhanara.app.domain.usecase.speech

import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.SpeechRepository
import javax.inject.Inject

/**
 * Use Case: دریافت سخنرانی با ID
 */
class GetSpeechByIdUseCase @Inject constructor(
    private val speechRepository: SpeechRepository
) {
    suspend operator fun invoke(id: String): Result<Speech> {
        return try {
            val speech = speechRepository.getSpeechById(id)
            if (speech != null) {
                Result.success(speech)
            } else {
                Result.failure(NoSuchElementException("سخنرانی یافت نشد"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}