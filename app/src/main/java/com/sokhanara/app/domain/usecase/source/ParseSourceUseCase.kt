package com.sokhanara.app.domain.usecase.source

import com.sokhanara.app.domain.model.Source
import com.sokhanara.app.domain.model.SourceType
import com.sokhanara.app.domain.repository.SourceRepository
import javax.inject.Inject

/**
 * Use Case: پردازش منبع
 */
class ParseSourceUseCase @Inject constructor(
    private val sourceRepository: SourceRepository
) {
    suspend operator fun invoke(
        uri: String,
        type: SourceType
    ): Result<Source> {
        return sourceRepository.parseSource(uri, type)
    }
}