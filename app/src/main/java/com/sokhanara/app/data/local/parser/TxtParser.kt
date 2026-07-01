package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject

class TxtParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun extractText(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return@withContext Result.failure(Exception("Cannot open file"))
            }

            val reader = BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            val sb = StringBuilder()
            reader.useLines { lines ->
                lines.forEach { line ->
                    sb.append(line).append('\n')
                }
            }
            val extracted = sb.toString().trim()
            if (extracted.isEmpty()) {
                return@withContext Result.failure(Exception("Empty text file"))
            }
            Timber.d("TXT parsed successfully: %d chars", extracted.length)
            Result.success(extracted)
        } catch (e: Exception) {
            Timber.e(e, "Error parsing TXT")
            Result.failure(Exception("Error processing TXT: ${e.message}"))
        }
    }
}
