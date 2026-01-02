package com.sokhanara.app.services.export

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.SpeechStage
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.util.Constants
import com.sokhanara.app.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * PDF Exporter
 * تولید فایل PDF از سخنرانی
 */
class PdfExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun exportToPdf(
        speech: Speech,
        theme: VisualTheme
    ): Result<String> {
        return try {
            Timber.d("Starting PDF export for speech: ${speech.title}")
            
            // ایجاد دایرکتوری خروجی
            val exportDir = FileUtil.getExportDirectory(context)
            val fileName = "speech_${speech.id}_${System.currentTimeMillis()}.pdf"
            val file = File(exportDir, fileName)
            
            // ایجاد سند PDF
            val document = Document(PageSize.A4)
            val writer = PdfWriter.getInstance(document, FileOutputStream(file))
            
            document.open()
            
            // تنظیم فونت فارسی
            val fontPath = getFontPath()
            val baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            
            val titleFont = Font(baseFont, 24f, Font.BOLD, getThemeColor(theme))
            val headerFont = Font(baseFont, 18f, Font.BOLD, BaseColor.BLACK)
            val bodyFont = Font(baseFont, 12f, Font.NORMAL, BaseColor.BLACK)
            
            // عنوان اصلی
            val title = Paragraph(speech.title, titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)
            
            // خط جداکننده
            document.add(Chunk.NEWLINE)
            
            // اضافه کردن محتوای ۵ مرحله
            speech.stages.entries.sortedBy { it.key.order }.forEach { (stage, content) ->
                // عنوان مرحله
                val stageTitle = Paragraph(stage.title, headerFont)
                stageTitle.alignment = Element.ALIGN_RIGHT
                stageTitle.spacingBefore = 15f
                stageTitle.spacingAfter = 10f
                document.add(stageTitle)
                
                // محتوای مرحله
                val stageContent = Paragraph(content, bodyFont)
                stageContent.alignment = Element.ALIGN_RIGHT
                stageContent.setLeading(0f, 1.5f) // فاصله بین خطوط
                document.add(stageContent)
                
                document.add(Chunk.NEWLINE)
            }
            
            // پاورقی
            val footer = Paragraph(
                "تولید شده توسط سخنرانی هوشمند",
                Font(baseFont, 8f, Font.ITALIC, BaseColor.GRAY)
            )
            footer.alignment = Element.ALIGN_CENTER
            document.add(footer)
            
            document.close()
            writer.close()
            
            Timber.d("PDF exported successfully: ${file.absolutePath}")
            Result.success(file.absolutePath)
            
        } catch (e: Exception) {
            Timber.e(e, "Error exporting PDF")
            Result.failure(Exception("خطا در تولید PDF: ${e.message}"))
        }
    }
    
    private fun getFontPath(): String {
        // کپی فونت از assets به cache
        val fontFile = File(context.cacheDir, "vazir.ttf")
        if (!fontFile.exists()) {
            context.assets.open("fonts/vazir_regular.ttf").use { input ->
                fontFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
        return fontFile.absolutePath
    }
    
    private fun getThemeColor(theme: VisualTheme): BaseColor {
        return when (theme) {
            VisualTheme.MOHARRAM -> BaseColor(28, 28, 28) // سیاه
            VisualTheme.RAMADAN -> BaseColor(0, 105, 92) // سبز آبی
            VisualTheme.EID -> BaseColor(56, 142, 60) // سبز
            VisualTheme.ACADEMIC -> BaseColor(26, 84, 144) // آبی
            VisualTheme.MINIMAL -> BaseColor(33, 33, 33) // خاکستری تیره
        }
    }
}