package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.ExportFormat
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.VisualTheme

/**
 * Export Repository Interface
 */
interface ExportRepository {
    
    suspend fun exportToPdf(
        speech: Speech,
        theme: VisualTheme
    ): Result<String>
    
    suspend fun exportToPowerPoint(
        speech: Speech,
        theme: VisualTheme
    ): Result<String>
    
    suspend fun exportToInfographic(
        speech: Speech,
        theme: VisualTheme
    ): Result<String>
    
    suspend fun generateChecklist(
        speech: Speech
    ): Result<String>
}