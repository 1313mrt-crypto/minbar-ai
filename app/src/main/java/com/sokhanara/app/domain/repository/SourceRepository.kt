package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.Source
import com.sokhanara.app.domain.model.SourceType

/**
 * Source Repository Interface
 */
interface SourceRepository {
    
    suspend fun parseSource(
        uri: String,
        type: SourceType
    ): Result<Source>
    
    suspend fun validateSource(source: Source): Result<Boolean>
    
    suspend fun extractText(source: Source): Result<String>
}