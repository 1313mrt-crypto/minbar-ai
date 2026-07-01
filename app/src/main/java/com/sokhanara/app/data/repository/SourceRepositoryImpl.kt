package com.sokhanara.app.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.LruCache
import com.sokhanara.app.data.local.parser.*
import com.sokhanara.app.domain.model.Source
import com.sokhanara.app.domain.model.SourceMetadata
import com.sokhanara.app.domain.model.SourceType
import com.sokhanara.app.domain.repository.SourceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

class SourceRepositoryImpl @Inject constructor(
    private val pdfParser: PdfParser,
    private val docxParser: DocxParser,
    private val txtParser: TxtParser,
    private val webParser: WebParser,
    private val contentResolver: ContentResolver
) : SourceRepository {

    companion object {
        private val parseSemaphore = Semaphore(3) // limit concurrent parses
        private const val MAX_BYTES = 50L * 1024L * 1024L // 50 MB
    }

    private val cache = LruCache<String, Source>(50)

    override suspend fun parseSource(uri: String, type: SourceType): Result<Source> {
        cache.get(uri)?.let { return Result.success(it) }

        return withContext(Dispatchers.IO) {
            parseSemaphore.withPermit {
                try {
                    if (type != SourceType.WEB_URL) {
                        val androidUri = Uri.parse(uri)
                        val size = querySize(androidUri)
                        if (size != null && size > MAX_BYTES) {
                            return@withContext Result.failure(Exception("File too large"))
                        }
                    }

                    val androidUri = Uri.parse(uri)

                    val textResult = when (type) {
                        SourceType.PDF -> pdfParser.extractText(androidUri)
                        SourceType.DOCX -> docxParser.extractText(androidUri)
                        SourceType.TXT -> txtParser.extractText(androidUri)
                        SourceType.WEB_URL -> webParser.extractText(uri)
                    }

                    textResult.fold(
                        onSuccess = { text ->
                            if (text.length > MAX_BYTES) {
                                return@withContext Result.failure(Exception("Parsed content too large"))
                            }
                            val source = Source(
                                id = "source_${System.currentTimeMillis()}",
                                type = type,
                                content = text,
                                title = extractTitle(uri, type),
                                metadata = SourceMetadata(
                                    fileSize = null,
                                    pageCount = null,
                                    author = null,
                                    createdDate = System.currentTimeMillis()
                                )
                            )
                            cache.put(uri, source)
                            Result.success(source)
                        },
                        onFailure = { error ->
                            Result.failure(error)
                        }
                    )

                } catch (e: Exception) {
                    Result.failure(e)
                }
            }
        }
    }

    private fun querySize(uri: Uri): Long? {
        return try {
            var cursor: Cursor? = null
            cursor = contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            val sizeIndex = cursor?.getColumnIndex(OpenableColumns.SIZE) ?: -1
            if (cursor != null && cursor.moveToFirst() && sizeIndex >= 0) {
                val size = cursor.getLong(sizeIndex)
                cursor.close()
                size
            } else {
                cursor?.close()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun validateSource(source: Source): Result<Boolean> {
        return try {
            if (source.content.length < 100) {
                return Result.failure(Exception("Content too short"))
            }
            if (source.content.length > 50_000_000) {
                return Result.failure(Exception("Content too large"))
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun extractText(source: Source): Result<String> {
        return Result.success(source.content)
    }

    private fun extractTitle(uri: String, type: SourceType): String {
        return when (type) {
            SourceType.WEB_URL -> {
                try {
                    Uri.parse(uri).host ?: "web_source"
                } catch (e: Exception) {
                    "web_source"
                }
            }
            else -> {
                try {
                    Uri.parse(uri).lastPathSegment ?: "source"
                } catch (e: Exception) {
                    "source"
                }
            }
        }
    }
}
