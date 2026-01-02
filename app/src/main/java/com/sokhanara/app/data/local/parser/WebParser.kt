package com.sokhanara.app.data.local.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

/**
 * Web Parser
 * استخراج متن از URL
 */
class WebParser @Inject constructor() {
    
    suspend fun extractText(url: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            // بررسی URL
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return@withContext Result.failure(Exception("URL نامعتبر است"))
            }
            
            // دریافت محتوا
            val connection = URL(url).openConnection()
            connection.connectTimeout = 10000 // 10 seconds
            connection.readTimeout = 10000
            
            val content = connection.getInputStream().bufferedReader().use { it.readText() }
            
            // حذف تگ‌های HTML (ساده)
            val text = content
                .replace(Regex("<script[^>]*>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
                .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "")
                .replace(Regex("<[^>]+>"), "")
                .replace(Regex("&nbsp;"), " ")
                .replace(Regex("&[a-z]+;"), "")
                .replace(Regex("\\s+"), " ")
                .trim()
            
            if (text.isEmpty()) {
                return@withContext Result.failure(Exception("محتوایی یافت نشد"))
            }
            
            Timber.d("Web content parsed successfully: ${text.length} characters")
            Result.success(text)
            
        } catch (e: Exception) {
            Timber.e(e, "Error parsing web URL")
            Result.failure(Exception("خطا در دریافت محتوا از وب: ${e.message}"))
        }
    }
}