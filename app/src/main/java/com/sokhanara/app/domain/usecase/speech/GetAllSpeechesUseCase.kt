package com.sokhanara.app.domain.usecase.speech

import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.SpeechRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case: دریافت تمام سخنرانی‌ها
 */
class GetAllSpeechesUseCase @Inject constructor(
    private val speechRepository: SpeechRepository
) {
    operator fun invoke(): Flow<List<Speech>> {
        return speechRepository.getAllSpeeches()
    }
}