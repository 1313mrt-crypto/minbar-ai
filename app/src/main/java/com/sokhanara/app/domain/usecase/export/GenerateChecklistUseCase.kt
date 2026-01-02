package com.sokhanara.app.domain.usecase.export

import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.ExportRepository
import javax.inject.Inject

/**
 * Use Case: تولید چک‌لیست تمرین
 */
class GenerateChecklistUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(speech: Speech): Result<String> {
        return exportRepository.generateChecklist(speech)
    }
}