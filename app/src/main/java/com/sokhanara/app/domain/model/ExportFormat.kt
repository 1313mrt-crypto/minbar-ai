package com.sokhanara.app.domain.model

/**
 * فرمت‌های خروجی
 */
enum class ExportFormat(
    val extension: String,
    val displayName: String,
    val mimeType: String
) {
    PDF(
        extension = ".pdf",
        displayName = "PDF",
        mimeType = "application/pdf"
    ),
    PPTX(
        extension = ".pptx",
        displayName = "PowerPoint",
        mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation"
    ),
    PNG(
        extension = ".png",
        displayName = "تصویر PNG",
        mimeType = "image/png"
    ),
    MP3(
        extension = ".mp3",
        displayName = "صوت MP3",
        mimeType = "audio/mpeg"
    );
}