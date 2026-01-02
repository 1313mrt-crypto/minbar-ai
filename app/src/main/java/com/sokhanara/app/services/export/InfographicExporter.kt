package com.sokhanara.app.services.export

import android.content.Context
import android.graphics.*
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.model.VisualTheme
import com.sokhanara.app.util.FileUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Infographic Exporter
 * تولید اینفوگرافی از سخنرانی
 */
class InfographicExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    suspend fun exportToInfographic(
        speech: Speech,
        theme: VisualTheme
    ): Result<String> {
        return try {
            Timber.d("Starting Infographic export for speech: ${speech.title}")
            
            // ابعاد اینفوگرافی
            val width = 1080
            val height = 1920 // فرمت Instagram Story
            
            // ایجاد bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // رنگ‌های تم
            val colors = getThemeColors(theme)
            
            // پس‌زمینه
            canvas.drawColor(colors.background)
            
            // رسم محتوا
            drawTitle(canvas, speech.title, colors, width)
            drawStages(canvas, speech, colors, width, height)
            drawFooter(canvas, colors, width, height)
            
            // ذخیره فایل
            val exportDir = FileUtil.getExportDirectory(context)
            val fileName = "infographic_${speech.id}_${System.currentTimeMillis()}.png"
            val file = File(exportDir, fileName)
            
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            
            bitmap.recycle()
            
            Timber.d("Infographic exported successfully: ${file.absolutePath}")
            Result.success(file.absolutePath)
            
        } catch (e: Exception) {
            Timber.e(e, "Error exporting Infographic")
            Result.failure(Exception("خطا در تولید اینفوگرافی: ${e.message}"))
        }
    }
    
    private fun drawTitle(canvas: Canvas, title: String, colors: InfographicColors, width: Int) {
        val paint = Paint().apply {
            color = colors.primary
            textSize = 72f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        // شکستن عنوان به چند خط در صورت نیاز
        val maxWidth = width - 160f
        val lines = breakTextIntoLines(title, paint, maxWidth)
        
        var y = 200f
        lines.forEach { line ->
            canvas.drawText(line, width / 2f, y, paint)
            y += 90f
        }
    }
    
    private fun drawStages(
        canvas: Canvas,
        speech: Speech,
        colors: InfographicColors,
        width: Int,
        height: Int
    ) {
        val startY = 400f
        val spacing = (height - 600f) / 5f
        
        speech.stages.entries.sortedBy { it.key.order }.forEachIndexed { index, (stage, content) ->
            val y = startY + (index * spacing)
            
            // دایره شماره
            val circlePaint = Paint().apply {
                color = colors.primary
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawCircle(120f, y, 50f, circlePaint)
            
            // شماره مرحله
            val numberPaint = Paint().apply {
                color = colors.onPrimary
                textSize = 48f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.drawText(stage.order.toString(), 120f, y + 18f, numberPaint)
            
            // عنوان مرحله
            val titlePaint = Paint().apply {
                color = colors.text
                textSize = 42f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText(stage.title, width - 80f, y + 18f, titlePaint)
            
            // خلاصه محتوا (50 کاراکتر اول)
            val summary = content.take(50) + "..."
            val contentPaint = Paint().apply {
                color = colors.secondaryText
                textSize = 28f
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            canvas.drawText(summary, width - 80f, y + 60f, contentPaint)
        }
    }
    
    private fun drawFooter(canvas: Canvas, colors: InfographicColors, width: Int, height: Int) {
        val paint = Paint().apply {
            color = colors.secondaryText
            textSize = 32f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        
        canvas.drawText(
            "سخنرانی هوشمند",
            width / 2f,
            height - 80f,
            paint
        )
    }
    
    private fun breakTextIntoLines(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""
        
        words.forEach { word ->
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            val width = paint.measureText(testLine)
            
            if (width > maxWidth && currentLine.isNotEmpty()) {
                lines.add(currentLine)
                currentLine = word
            } else {
                currentLine = testLine
            }
        }
        
        if (currentLine.isNotEmpty()) {
            lines.add(currentLine)
        }
        
        return lines
    }
    
    private fun getThemeColors(theme: VisualTheme): InfographicColors {
        return when (theme) {
            VisualTheme.MOHARRAM -> InfographicColors(
                primary = Color.parseColor("#1C1C1C"),
                onPrimary = Color.parseColor("#FFFFFF"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121"),
                secondaryText = Color.parseColor("#757575")
            )
            VisualTheme.RAMADAN -> InfographicColors(
                primary = Color.parseColor("#00695C"),
                onPrimary = Color.parseColor("#FFFFFF"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121"),
                secondaryText = Color.parseColor("#757575")
            )
            VisualTheme.EID -> InfographicColors(
                primary = Color.parseColor("#388E3C"),
                onPrimary = Color.parseColor("#FFFFFF"),
                background = Color.parseColor("#FAFAFA"),
                text = Color.parseColor("#212121"),
                secondaryText = Color.parseColor("#757575")
            )
            VisualTheme.ACADEMIC -> InfographicColors(
                primary = Color.parseColor("#1A5490"),
                onPrimary = Color.parseColor("#FFFFFF"),
                background = Color.parseColor("#FFFFFF"),
                text = Color.parseColor("#212121"),
                secondaryText = Color.parseColor("#757575")
            )
            VisualTheme.MINIMAL -> InfographicColors(
                primary = Color.parseColor("#212121"),
                onPrimary = Color.parseColor("#FFFFFF"),
                background = Color.parseColor("#FFFFFF"),
                text = Color.parseColor("#212121"),
                secondaryText = Color.parseColor("#757575")
            )
        }
    }
    
    data class InfographicColors(
        val primary: Int,
        val onPrimary: Int,
        val background: Int,
        val text: Int,
        val secondaryText: Int
    )
}