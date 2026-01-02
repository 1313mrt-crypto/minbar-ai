package com.sokhanara.app.domain.usecase.library

import com.sokhanara.app.domain.repository.LibraryRepository
import javax.inject.Inject

/**
 * Use Case: پیشنهاد منابع
 */
class SuggestSourcesUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository
) {
    suspend operator fun invoke(topicId: String): Result<List<String>> {
        return try {
            val sources = libraryRepository.suggestSources(topicId)
            Result.success(sources)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}