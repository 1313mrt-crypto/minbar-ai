package com.sokhanara.app.domain.usecase.library

import com.sokhanara.app.domain.model.Topic
import com.sokhanara.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use Case: دریافت موضوعات
 */
class GetTopicsUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    operator fun invoke(): Flow<List<Topic>> {
        return libraryRepository.getAllTopics()
    }
}