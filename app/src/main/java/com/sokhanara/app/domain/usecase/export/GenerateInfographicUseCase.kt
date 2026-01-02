package com.sokhanara.app.domain.usecase.export

import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.ExportRepository
import javax.inject.Inject

/**
 * Use Case: تولید اینفوگرافی
 */
class GenerateInfographicUseCase @Inject constructor(
    private val exportRepository: ExportRepository
) {
    suspend operator fun invoke(speech: Speech): Result<String> {
        return exportRepository.exportToInfographic(
            speech = speech,
            theme = speech.theme
        )
    }
}