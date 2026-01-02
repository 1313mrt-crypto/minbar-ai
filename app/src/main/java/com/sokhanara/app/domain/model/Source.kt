package com.sokhanara.app.domain.model

/**
 * منابع کاربر
 */
data class Source(
    val id: String,
    val type: SourceType,
    val content: String,
    val title: String?,
    val metadata: SourceMetadata?
)

enum class SourceType {
    PDF,
    DOCX,
    TXT,
    WEB_URL
}

data class SourceMetadata(
    val fileSize: Long?,
    val pageCount: Int?,
    val author: String?,
    val createdDate: Long?
)