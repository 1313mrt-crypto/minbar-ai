package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

/**
 * DOCX Parser
 * استخراج متن از Word
 */
class DocxParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun extractText(uri: Uri): Result<String> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                return Result.failure(Exception("نمی‌توان فایل را باز کرد"))
            }
            
            val document = XWPFDocument(inputStream)
            val stringBuilder = StringBuilder()
            
            // استخراج پاراگراف‌ها
            document.paragraphs.forEach { paragraph ->
                val text = paragraph.text
                if (text.isNotBlank()) {
                    stringBuilder.append(text)
                    stringBuilder.append("\n")
                }
            }
            
            // استخراج متن از جداول
            document.tables.forEach { table ->
                table.rows.forEach { row ->
                    row.tableCells.forEach { cell ->
                        val text = cell.text
                        if (text.isNotBlank()) {
                            stringBuilder.append(text)
                            stringBuilder.append(" ")
                        }
                    }
                    stringBuilder.append("\n")
                }
            }
            
            document.close()
            inputStream.close()
            
            val extractedText = stringBuilder.toString().trim()
            
            if (extractedText.isEmpty()) {
                return Result.failure(Exception("فایل خالی است"))
            }
            
            Timber.d("DOCX parsed successfully: ${extractedText.length} characters")
            Result.success(extractedText)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing DOCX")
            Result.failure(Exception("خطا در پردازش Word: ${e.message}"))
        }
    }
}