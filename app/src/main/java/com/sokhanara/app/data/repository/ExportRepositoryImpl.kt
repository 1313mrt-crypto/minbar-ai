package com.sokhanara.app.data.repository

import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.domain.repository.ExportRepository
import com.sokhanara.app.services.export.*
import javax.inject.Inject

/**
 * پیاده‌سازی Export Repository
 */
class ExportRepositoryImpl @Inject constructor(
    private val pdfExporter: PdfExporter,
    private val powerPointExporter: PowerPointExporter,
    private val infographicExporter: InfographicExporter,
    private val checklistGenerator: ChecklistGenerator
) : ExportRepository {
    
    override suspend fun exportToPdf(speech: Speech, theme: VisualTheme): Result<String> {
        return pdfExporter.exportToPdf(speech, theme)
    }
    
    override suspend fun exportToPowerPoint(speech: Speech, theme: VisualTheme): Result<String> {
        return powerPointExporter.exportToPowerPoint(speech, theme)
    }
    
    override suspend fun exportToInfographic(speech: Speech, theme: VisualTheme): Result<String> {
        return infographicExporter.exportToInfographic(speech, theme)
    }
    
    override suspend fun generateChecklist(speech: Speech): Result<String> {
        return checklistGenerator.generateChecklist(speech)
    }
}