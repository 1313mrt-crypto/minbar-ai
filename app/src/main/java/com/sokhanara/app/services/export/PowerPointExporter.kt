package com.sokhanara.app.services.export

import android.content.Context
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.xslf.usermodel.*
import timber.log.Timber
import java.awt.Color
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
            
            // تنظیم اندازه اسلاید (16:9)
            ppt.pageSize = java.awt.Dimension(960, 540)
            
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
        slide.background.fillColor = colors.background
        
        // عنوان
        val titleBox = slide.createTextBox()
        titleBox.setAnchor(java.awt.Rectangle(50, 150, 860, 100))
        titleBox.text = title
        
        val titleParagraph = titleBox.textParagraphs[0]
        titleParagraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val titleRun = titleParagraph.textRuns[0]
        titleRun.fontSize = 44.0
        titleRun.fontColor = colors.primary
        titleRun.isBold = true
        
        // زیرنویس
        val subtitleBox = slide.createTextBox()
        subtitleBox.setAnchor(java.awt.Rectangle(50, 300, 860, 50))
        subtitleBox.text = "تولید شده توسط سخنرانی هوشمند"
        
        val subtitleParagraph = subtitleBox.textParagraphs[0]
        subtitleParagraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val subtitleRun = subtitleParagraph.textRuns[0]
        subtitleRun.fontSize = 18.0
        subtitleRun.fontColor = colors.secondary
    }
    
    private fun createContentSlide(
        ppt: XMLSlideShow,
        title: String,
        content: String,
        colors: ThemeColors
    ) {
        val slide = ppt.createSlide()
        
        // پس‌زمینه
        slide.background.fillColor = colors.background
        
        // عنوان
        val titleBox = slide.createTextBox()
        titleBox.setAnchor(java.awt.Rectangle(50, 30, 860, 60))
        titleBox.text = title
        
        val titleParagraph = titleBox.textParagraphs[0]
        titleParagraph.textAlign = TextParagraph.TextAlign.RIGHT
        
        val titleRun = titleParagraph.textRuns[0]
        titleRun.fontSize = 32.0
        titleRun.fontColor = colors.primary
        titleRun.isBold = true
        
        // محتوا
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
                run.fontColor = colors.text
            }
        }
    }
    
    private fun createEndSlide(
        ppt: XMLSlideShow,
        colors: ThemeColors
    ) {
        val slide = ppt.createSlide()
        
        slide.background.fillColor = colors.background
        
        val thanksBox = slide.createTextBox()
        thanksBox.setAnchor(java.awt.Rectangle(50, 200, 860, 100))
        thanksBox.text = "با تشکر از توجه شما"
        
        val paragraph = thanksBox.textParagraphs[0]
        paragraph.textAlign = TextParagraph.TextAlign.CENTER
        
        val run = paragraph.textRuns[0]
        run.fontSize = 40.0
        run.fontColor = colors.primary
        run.isBold = true
    }
    
    private fun getThemeColors(theme: VisualTheme): ThemeColors {
        return when (theme) {
            VisualTheme.MOHARRAM -> ThemeColors(
                primary = Color(28, 28, 28),
                secondary = Color(198, 40, 40),
                background = Color(250, 250, 250),
                text = Color(33, 33, 33)
            )
            VisualTheme.RAMADAN -> ThemeColors(
                primary = Color(0, 105, 92),
                secondary = Color(255, 213, 79),
                background = Color(250, 250, 250),
                text = Color(33, 33, 33)
            )
            VisualTheme.EID -> ThemeColors(
                primary = Color(56, 142, 60),
                secondary = Color(255, 249, 196),
                background = Color(250, 250, 250),
                text = Color(33, 33, 33)
            )
            VisualTheme.ACADEMIC -> ThemeColors(
                primary = Color(26, 84, 144),
                secondary = Color(212, 175, 55),
                background = Color(255, 255, 255),
                text = Color(33, 33, 33)
            )
            VisualTheme.MINIMAL -> ThemeColors(
                primary = Color(33, 33, 33),
                secondary = Color(117, 117, 117),
                background = Color(255, 255, 255),
                text = Color(33, 33, 33)
            )
        }
    }
    
    data class ThemeColors(
        val primary: Color,
        val secondary: Color,
        val background: Color,
        val text: Color
    )
}