package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

/**
 * PDF Parser
 * استخراج متن از PDF - استفاده از PDFBox برای Android
 */
class PdfParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    init {
        PDFBoxResourceLoader.init(context)
    }
    
    fun extractText(uri: Uri): Result<String> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                return Result.failure(Exception("نمی‌توان فایل را باز کرد"))
            }
            
            val document = PDDocument.load(inputStream)
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            inputStream.close()
            
            val extractedText = text.trim()
            
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
            val document = PDDocument.load(java.io.File(filePath))
            val stripper = PDFTextStripper()
            val text = stripper.getText(document)
            document.close()
            
            val extractedText = text.trim()
            Result.success(extractedText)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing PDF from path")
            Result.failure(e)
        }
    }
}