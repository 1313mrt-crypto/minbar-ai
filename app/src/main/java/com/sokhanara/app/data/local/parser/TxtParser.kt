package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

/**
 * TXT Parser
 * استخراج متن از فایل متنی
 */
class TxtParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    fun extractText(uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            
            if (inputStream == null) {
                return Result.failure(Exception("نمی‌توان فایل را باز کرد"))
            }
            
            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val stringBuilder = StringBuilder()
            
            reader.useLines { lines ->
                lines.forEach { line ->
                    stringBuilder.append(line)
                    stringBuilder.append("\n")
                }
            }
            
            inputStream.close()
            
            val extractedText = stringBuilder.toString().trim()
            
            if (extractedText.isEmpty()) {
                return Result.failure(Exception("فایل خالی است"))
            }
            
            Timber.d("TXT parsed successfully: ${extractedText.length} characters")
            Result.success(extractedText)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing TXT")
            Result.failure(Exception("خطا در پردازش فایل متنی: ${e.message}"))
        }
    }
}