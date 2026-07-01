package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class PdfParser @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val pdfBoxInitialized by lazy {
        try {
            PDFBoxResourceLoader.init(context)
            true
        } catch (t: Throwable) {
            Timber.e(t, "PDFBox init failed")
            false
        }
    }

    suspend fun extractText(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (!pdfBoxInitialized) {
                Timber.w("PDFBox may not have been initialized")
            }

            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return@withContext Result.failure(Exception("Cannot open file"))
            }

            PDDocument.load(inputStream).use { document ->
                val stripper = PDFTextStripper()
                val text = stripper.getText(document).trim()
                if (text.isEmpty()) {
                    return@withContext Result.failure(Exception("No text found in PDF"))
                }
                Timber.d("PDF parsed successfully: %d characters", text.length)
                Result.success(text)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing PDF")
            Result.failure(Exception("Error parsing PDF: ${e.message}"))
        }
    }

    suspend fun extractTextFromPath(filePath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            PDDocument.load(java.io.File(filePath)).use { document ->
                val stripper = PDFTextStripper()
                val text = stripper.getText(document).trim()
                Result.success(text)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing PDF from path")
            Result.failure(e)
        }
    }
}
