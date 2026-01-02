package com.sokhanara.app.services.export

import android.content.Context
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Checklist Generator
 * تولید چک‌لیست تمرین سخنرانی
 */
class ChecklistGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun generateChecklist(speech: Speech): Result<String> {
        return try {
            Timber.d("Generating checklist for speech: ${speech.title}")
            
            val exportDir = FileUtil.getExportDirectory(context)
            val fileName = "checklist_${speech.id}_${System.currentTimeMillis()}.pdf"
            val file = File(exportDir, fileName)
            
            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(file))
            
            document.open()
            
            // تنظیم فونت
            val fontPath = getFontPath()
            val baseFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED)
            
            val titleFont = Font(baseFont, 20f, Font.BOLD)
            val headerFont = Font(baseFont, 14f, Font.BOLD)
            val bodyFont = Font(baseFont, 11f, Font.NORMAL)
            
            // عنوان
            val title = Paragraph("چک‌لیست تمرین سخنرانی", titleFont)
            title.alignment = Element.ALIGN_CENTER
            title.spacingAfter = 20f
            document.add(title)
            
            // عنوان سخنرانی
            val speechTitle = Paragraph("موضوع: ${speech.title}", headerFont)
            speechTitle.alignment = Element.ALIGN_RIGHT
            speechTitle.spacingAfter = 15f
            document.add(speechTitle)
            
            // بخش‌های چک‌لیست
            addChecklistSection(document, "۱. آماده‌سازی قبل از سخنرانی", listOf(
                "☐ محتوای تمام ۵ مرحله را حداقل ۳ بار مطالعه کنید",
                "☐ نکات کلیدی را یادداشت کنید",
                "☐ زمان هر مرحله را تخمین بزنید",
                "☐ مکان سخنرانی را بررسی کنید",
                "☐ لباس مناسب را آماده کنید"
            ), headerFont, bodyFont)
            
            addChecklistSection(document, "۲. تمرین صوتی", listOf(
                "☐ سخنرانی را با صدای بلند تمرین کنید",
                "☐ تلفظ کلمات دشوار را تمرین کنید",
                "☐ سرعت گفتار را کنترل کنید (120-180 کلمه در دقیقه)",
                "☐ مکث‌های مناسب را تمرین کنید",
                "☐ فراز و فرود صدا را تمرین کنید"
            ), headerFont, bodyFont)
            
            addChecklistSection(document, "۳. تمرین بدن و حرکات", listOf(
                "☐ وضعیت ایستادن صحیح",
                "☐ تماس چشمی با مخاطبان",
                "☐ استفاده از حرکات دست به‌جا",
                "☐ حرکت در صحنه (در صورت نیاز)",
                "☐ کنترل حالات چهره"
            ), headerFont, bodyFont)
            
            addChecklistSection(document, "۴. قبل از شروع سخنرانی", listOf(
                "☐ آب بنوشید",
                "☐ چند تمرین تنفسی انجام دهید",
                "☐ ذهن را آرام کنید",
                "☐ لبخند بزنید و آماده شوید"
            ), headerFont, bodyFont)
            
            addChecklistSection(document, "۵. بعد از سخنرانی", listOf(
                "☐ نقاط قوت را یادداشت کنید",
                "☐ نقاط قابل بهبود را شناسایی کنید",
                "☐ بازخورد مخاطبان را دریافت کنید",
                "☐ برای سخنرانی بعدی برنامه‌ریزی کنید"
            ), headerFont, bodyFont)
            
            document.close()
            
            Timber.d("Checklist generated successfully: ${file.absolutePath}")
            Result.success(file.absolutePath)
            
        } catch (e: Exception) {
            Timber.e(e, "Error generating checklist")
            Result.failure(e)
        }
    }
    
    private fun addChecklistSection(
        document: Document,
        title: String,
        items: List<String>,
        headerFont: Font,
        bodyFont: Font
    ) {
        // عنوان بخش
        val sectionTitle = Paragraph(title, headerFont)
        sectionTitle.alignment = Element.ALIGN_RIGHT
        sectionTitle.spacingBefore = 10f
        sectionTitle.spacingAfter = 5f
        document.add(sectionTitle)
        
        // آیتم‌ها
        items.forEach { item ->
            val paragraph = Paragraph(item, bodyFont)
            paragraph.alignment = Element.ALIGN_RIGHT
            paragraph.indentationRight = 20f
            paragraph.spacingAfter = 5f
            document.add(paragraph)
        }
        
        document.add(Chunk.NEWLINE)
    }
    
    private fun getFontPath(): String {
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
}