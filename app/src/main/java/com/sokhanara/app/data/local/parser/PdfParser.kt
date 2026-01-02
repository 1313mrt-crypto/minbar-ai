package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

/**
 * PDF Parser
 * استخراج متن از PDF
 */
class PdfParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun extractText(uri: Uri): Result<String> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                return Result.failure(Exception("نمی‌توان فایل را باز کرد"))
            }
            
            val reader = PdfReader(inputStream)
            val stringBuilder = StringBuilder()
            
            for (i in 1..reader.numberOfPages) {
                val text = PdfTextExtractor.getTextFromPage(reader, i)
                stringBuilder.append(text)
                stringBuilder.append("\n\n")
            }
            
            reader.close()
            inputStream.close()
            
            val extractedText = stringBuilder.toString().trim()
            
            if (extractedText.isEmpty()) {
                return Result.failure(Exception("فایل خالی است یا متنی در آن یافت نشد"))
            }
            
            Timber.d("PDF parsed successfully: ${extractedText.length} characters")
            Result.success(extractedText)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing PDF")
            Result.failure(Exception("خطا در پردازش PDF: ${e.message}"))
        }
    }
    
    fun extractTextFromPath(filePath: String): Result<String> {
        return try {
            val reader = PdfReader(filePath)
            val stringBuilder = StringBuilder()
            
            for (i in 1..reader.numberOfPages) {
                val text = PdfTextExtractor.getTextFromPage(reader, i)
                stringBuilder.append(text)
                stringBuilder.append("\n\n")
            }
            
            reader.close()
            
            val extractedText = stringBuilder.toString().trim()
            Result.success(extractedText)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing PDF from path")
            Result.failure(e)
        }
    }
}