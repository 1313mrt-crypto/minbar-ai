package com.sokhanara.app.data.local.parser

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class WebParser @Inject constructor() {

    private val MAX_BYTES = 5 * 1024 * 1024 // 5 MB cap for web fetch

    suspend fun extractText(url: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                return@withContext Result.failure(Exception("Invalid URL"))
            }

            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
                requestMethod = "GET"
                setRequestProperty("User-Agent", "MinbarAI/1.0")
                instanceFollowRedirects = true
            }

            val contentLength = connection.getHeaderFieldLong("Content-Length", -1L)
            if (contentLength > MAX_BYTES) {
                connection.disconnect()
                return@withContext Result.failure(Exception("Content too large"))
            }

            val stream = BufferedInputStream(connection.inputStream)
            val buffer = ByteArray(8 * 1024)
            val sb = StringBuilder()
            var totalRead = 0
            var read: Int

            while (stream.read(buffer).also { read = it } != -1) {
                totalRead += read
                if (totalRead > MAX_BYTES) {
                    stream.close()
                    connection.disconnect()
                    return@withContext Result.failure(Exception("Content exceeds maximum allowed size"))
                }
                sb.append(String(buffer, 0, read))
            }

            stream.close()
            connection.disconnect()

            val text = sb.toString()
                .replace(Regex("<script[^>]*>.*?</script>", RegexOption.DOT_MATCHES_ALL), "")
                .replace(Regex("<style[^>]*>.*?</style>", RegexOption.DOT_MATCHES_ALL), "")
                .replace(Regex("<[^>]+>"), "")
                .replace(Regex("&nbsp;"), " ")
                .replace(Regex("&[a-z]+;"), "")
                .replace(Regex("\\s+"), " ")
                .trim()

            if (text.isEmpty()) {
                return@withContext Result.failure(Exception("No text content"))
            }

            Timber.d("Web content parsed successfully: %d characters", text.length)
            Result.success(text)

        } catch (e: Exception) {
            Timber.e(e, "Error parsing web URL")
            Result.failure(Exception("Error fetching web content: ${e.message}"))
        }
    }
}
