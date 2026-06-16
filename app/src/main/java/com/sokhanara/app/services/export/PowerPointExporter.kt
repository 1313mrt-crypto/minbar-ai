package com.sokhanara.app.services.export

import android.content.Context
import android.graphics.Color
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.xslf.usermodel.*
import org.apache.poi.sl.draw.*
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * PowerPoint Exporter
 * تولید فایل PowerPoint از سخنرانی
 */
class PowerPointExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun exportToPowerPoint(
        speech: Speech,
        theme: VisualTheme
    ): Result<String> {
        return try {
            Timber.d("Starting PowerPoint export for speech: ${speech.title}")
            
            // ایجاد دایرکتوری خروجی
            val exportDir = FileUtil.getExportDirectory(context)
            val fileName = "speech_${speech.id}_${System.currentTimeMillis()}.pptx"
            val file = File(exportDir, fileName)
            
            // ایجاد presentation
            val ppt = XMLSlideShow()
            
            // تنظیم اندازه اسلاید (16:9) - using double array for dimensions
            val pageSize = java.awt.Dimension(960, 540)
            ppt.pageSize = pageSize
            
            val themeColors = getThemeColors(theme)
            
            // اسلاید عنوان
            createTitleSlide(ppt, speech.title, themeColors)
            
            // اسلاید برای هر مرحله
            speech.stages.entries.sortedBy { it.key.order }.forEach { (stage, content) ->
                createContentSlide(
                    ppt = ppt,
                    title = "${stage.order}. ${stage.title}",
                    content = content,
                    colors = themeColors
                )
            }
            
            // اسلاید پایانی
            createEndSlide(ppt, themeColors)
            
            // ذخیره فایل
            FileOutputStream(file).use { out ->
                ppt.write(out)
            }
            ppt.close()
            
            Timber.d("PowerPoint exported successfully: ${file.absolutePath}")
            Result.success(file.absolutePath)
            
        } catch (e: Exception) {
            Timber.e(e, "Error exporting PowerPoint")
            Result.failure(Exception("خطا در تولید PowerPoint: ${e.message}"))
        }
    }
    
    private fun createTitleSlide(
        ppt: XMLSlideShow,
        title: String,
        colors: ThemeColors
    ) {
        val slide = ppt.createSlide()
        
        // پس‌زمینه
        slide.background.fillColor = java.awt.Color(colors.background)
        
        // عنوان
        val titleBox = slide.createTextBox()
        titleBox.setAnchor(java.awt.Rectangle(50, 150, 860, 100))
        titleBox.text = title
        
        val titleParagraph = titleBox.textParagraphs[0]
        titleParagraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val titleRun = titleParagraph.textRuns[0]
        titleRun.fontSize = 44.0
        titleRun.fontColor = java.awt.Color(colors.primary)
        titleRun.isBold = true
        
        // زیرنویس
        val subtitleBox = slide.createTextBox()
        subtitleBox.setAnchor(java.awt.Rectangle(50, 300, 860, 50))
        subtitleBox.text = "تولید شده توسط سخنرانی هوشمند"
        
        val subtitleParagraph = subtitleBox.textParagraphs[0]
        subtitleParagraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val subtitleRun = subtitleParagraph.textRuns[0]
        subtitleRun.fontSize = 18.0
        subtitleRun.fontColor = java.awt.Color(colors.secondary)
    }
    
    private fun createContentSlide(
        ppt: XMLSlideShow,
        title: String,
        content: String,
        colors: ThemeColors
    ) {
        val slide = ppt.createSlide()
        
        // پس‌زمینه
        slide.background.fillColor = java.awt.Color(colors.background)
        
        // عنوان
        val titleBox = slide.createTextBox()
        titleBox.setAnchor(java.awt.Rectangle(50, 30, 860, 60))
        titleBox.text = title
        
        val titleParagraph = titleBox.textParagraphs[0]
        titleParagraph.textAlign = TextParagraph.TextAlign.RIGHT
        
        val titleRun = titleParagraph.textRuns[0]
        titleRun.fontSize = 32.0
        titleRun.fontColor = java.awt.Color(colors.primary)
        titleRun.isBold = true
        
        // محتوا - استفاده از RichTextRun برای پشتیبانی بهتر
        val contentBox = slide.createTextBox()
        contentBox.setAnchor(java.awt.Rectangle(50, 120, 860, 380))
        
        // تقسیم محتوا به پاراگراف‌ها
        val paragraphs = content.split("\n\n")
        contentBox.text = paragraphs.take(5).joinToString("\n\n") // حداکثر 5 پاراگراف
        
        contentBox.textParagraphs.forEach { paragraph ->
            paragraph.textAlign = TextParagraph.TextAlign.RIGHT
            paragraph.lineSpacing = 150.0 // فاصله بین خطوط 150%
            
            paragraph.textRuns.forEach { run ->
                run.fontSize = 18.0
                run.fontColor = java.awt.Color(colors.text)
            }
        }
    }
    
    private fun createEndSlide(
        ppt: XMLSlideShow,
        colors: ThemeColors
    ) {
        val slide = ppt.createSlide()
        
        slide.background.fillColor = java.awt.Color(colors.background)
        
        val thanksBox = slide.createTextBox()
        thanksBox.setAnchor(java.awt.Rectangle(50, 200, 860, 100))
        thanksBox.text = "با تشکر از توجه شما"
        
        val paragraph = thanksBox.textParagraphs[0]
        paragraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val run = paragraph.textRuns[0]
        run.fontSize = 40.0
        run.fontColor = java.awt.Color(colors.primary)
        run.isBold = true
    }
    
    private fun getThemeColors(theme: VisualTheme): ThemeColors {
        return when (theme) {
            VisualTheme.MOHARRAM -> ThemeColors(
                primary = Color.parseColor("#1C1C1C"),
                secondary = Color.parseColor("#C62828"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121")
            )
            VisualTheme.RAMADAN -> ThemeColors(
                primary = Color.parseColor("#00695C"),
                secondary = Color.parseColor("#FFD54F"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121")
            )
            VisualTheme.EID -> ThemeColors(
                primary = Color.parseColor("#388E3C"),
                secondary = Color.parseColor("#FFF9C4"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121")
            )
            VisualTheme.ACADEMIC -> ThemeColors(
                primary = Color.parseColor("#1A5490"),
                secondary = Color.parseColor("#D4AF37"),
                background = Color.parseColor("#FFFFFF"),
                text = Color.parseColor("#212121")
            )
            VisualTheme.MINIMAL -> ThemeColors(
                primary = Color.parseColor("#212121"),
                secondary = Color.parseColor("#757575"),
                background = Color.parseColor("#FFFFFF"),
                text = Color.parseColor("#212121")
            )
        }
    }
    
    data class ThemeColors(
        val primary: Int,
        val secondary: Int,
        val background: Int,
        val text: Int
    )
}