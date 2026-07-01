package com.sokhanara.app.data.local.parser

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xwpf.usermodel.XWPFDocument
import timber.log.Timber
import java.io.InputStream
import javax.inject.Inject

class DocxParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun extractText(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                return@withContext Result.failure(Exception("Cannot open file"))
            }

            XWPFDocument(inputStream).use { document ->
                val sb = StringBuilder()
                document.paragraphs.forEach { p ->
                    val t = p.text
                    if (t.isNotBlank()) {
                        sb.append(t).append('\n')
                    }
                }
                document.tables.forEach { table ->
                    table.rows.forEach { row ->
                        row.tableCells.forEach { cell ->
                            val ct = cell.text
                            if (ct.isNotBlank()) {
                                sb.append(ct).append(' ')
                            }
                        }
                        sb.append('\n')
                    }
                }
                val extracted = sb.toString().trim()
                if (extracted.isEmpty()) {
                    return@withContext Result.failure(Exception("Empty DOCX"))
                }
                Timber.d("DOCX parsed: %d chars", extracted.length)
                Result.success(extracted)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error parsing DOCX")
            Result.failure(Exception("Error processing DOCX: ${e.message}"))
        }
    }
}
