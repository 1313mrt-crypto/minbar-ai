package com.sokhanara.app.data.repository

import android.net.Uri
import com.sokhanara.app.data.local.parser.*
import com.sokhanara.app.domain.model.Source
import com.sokhanara.app.domain.model.SourceMetadata
import com.sokhanara.app.domain.model.SourceType
import com.sokhanara.app.domain.repository.SourceRepository
import javax.inject.Inject

/**
 * پیاده‌سازی Source Repository
 */
class SourceRepositoryImpl @Inject constructor(
    private val pdfParser: PdfParser,
    private val docxParser: DocxParser,
    private val txtParser: TxtParser,
    private val webParser: WebParser
) : SourceRepository {
    
    override suspend fun parseSource(uri: String, type: SourceType): Result<Source> {
        return try {
            val androidUri = Uri.parse(uri)
            
            val textResult = when (type) {
                SourceType.PDF -> pdfParser.extractText(androidUri)
                SourceType.DOCX -> docxParser.extractText(androidUri)
                SourceType.TXT -> txtParser.extractText(androidUri)
                SourceType.WEB_URL -> webParser.extractText(uri)
            }
            
            textResult.fold(
                onSuccess = { text ->
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
    
    override suspend fun validateSource(source: Source): Result<Boolean> {
        return try {
            // بررسی طول محتوا
            if (source.content.length < 100) {
                return Result.failure(Exception("محتوا بسیار کوتاه است (حداقل ۱۰۰ کاراکتر)"))
            }
            
            // بررسی حجم (حداکثر 50 مگابایت معادل حدود 50 میلیون کاراکتر)
            if (source.content.length > 50_000_000) {
                return Result.failure(Exception("محتوا بسیار بزرگ است (حداکثر ۵۰ مگابایت)"))
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
                    Uri.parse(uri).host ?: "منبع وب"
                } catch (e: Exception) {
                    "منبع وب"
                }
            }
            else -> {
                try {
                    Uri.parse(uri).lastPathSegment ?: "منبع"
                } catch (e: Exception) {
                    "منبع"
                }
            }
        }
    }
}