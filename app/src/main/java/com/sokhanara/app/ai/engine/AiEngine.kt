package com.sokhanara.app.ai.engine

import com.sokhanara.app.domain.model.*
import timber.log.Timber
import javax.inject.Inject

/**
 * AI Engine
 * موتور اصلی هوش مصنوعی
 */
class AiEngine @Inject constructor(
    private val promptBuilder: PromptBuilder
) {
    
    /**
     * تولید محتوا برای یک مرحله خاص
     */
    suspend fun generateStageContent(
        stage: SpeechStage,
        topic: String,
        sources: List<Source>,
        style: SpeechStyle,
        language: Language
    ): Result<String> {
        return try {
            Timber.d("Generating content for stage: ${stage.title}")
            
            // ترکیب محتوای منابع
            val sourceContent = sources.joinToString("\n\n---\n\n") { source ->
                "منبع: ${source.title ?: "بدون عنوان"}\n${source.content}"
            }
            
            // ساخت پرامپت
            val prompt = promptBuilder.buildPromptForStage(
                stage = stage,
                topic = topic,
                sourceContent = sourceContent,
                style = style,
                language = language.code
            )
            
            // TODO: اینجا باید API واقعی هوش مصنوعی صدا زده بشه
            // فعلاً یک محتوای نمونه برمی‌گردونیم
            
            val generatedContent = generateMockContent(stage, topic)
            
            Timber.d("Content generated successfully for ${stage.title}")
            Result.success(generatedContent)
            
        } catch (e: Exception) {
            Timber.e(e, "Error generating content for stage: ${stage.title}")
            Result.failure(e)
        }
    }
    
    /**
     * تولید محتوا برای تمام ۵ مرحله
     */
    suspend fun generateFullSpeech(
        topic: String,
        sources: List<Source>,
        style: SpeechStyle,
        language: Language
    ): Result<Map<SpeechStage, String>> {
        return try {
            Timber.d("Generating full speech for topic: $topic")
            
            val stages = mutableMapOf<SpeechStage, String>()
            
            // تولید هر مرحله به ترتیب
            SpeechStage.values().sortedBy { it.order }.forEach { stage ->
                val result = generateStageContent(
                    stage = stage,
                    topic = topic,
                    sources = sources,
                    style = style,
                    language = language
                )
                
                result.fold(
                    onSuccess = { content ->
                        stages[stage] = content
                    },
                    onFailure = { error ->
                        return Result.failure(
                            Exception("خطا در تولید ${stage.title}: ${error.message}")
                        )
                    }
                )
            }
            
            Timber.d("Full speech generated successfully")
            Result.success(stages)
            
        } catch (e: Exception) {
            Timber.e(e, "Error generating full speech")
            Result.failure(e)
        }
    }
    
    /**
     * تولید محتوای نمونه (موقت - تا API واقعی متصل بشه)
     */
    private fun generateMockContent(stage: SpeechStage, topic: String): String {
        return when (stage) {
            SpeechStage.MOTIVATION -> """
                در دنیایی که هر روز با چالش‌های جدیدی روبرو می‌شویم، آیا تا به حال به این فکر کرده‌اید که چرا برخی افراد موفق می‌شوند و برخی دیگر شکست می‌خورند؟
                
                موضوع $topic نه تنها یک بحث علمی است، بلکه راهی است برای تغییر زندگی ما. وقتی به اهمیت این موضوع پی می‌بریم، متوجه می‌شویم که چقدر در زندگی روزمره‌مان تأثیرگذار است.
            """.trimIndent()
            
            SpeechStage.CONVICTION -> """
                اولین دلیل: قرآن کریم در آیه شریفه به ما می‌گوید که این موضوع از اهمیت ویژه‌ای برخوردار است.
                
                دومین دلیل: احادیث معتبر نیز بر این نکته تأکید دارند که این موضوع می‌تواند زندگی ما را متحول کند.
                
                سومین دلیل: تجربیات واقعی افرادی که در این مسیر گام برداشته‌اند نشان می‌دهد که این راه به نتایج مثبت منجر شده است.
            """.trimIndent()
            
            SpeechStage.EMOTION -> """
                تصور کنید فردی که سال‌ها در تاریکی بوده است و ناگهان نوری او را احاطه می‌کند. همین حس را زمانی تجربه می‌کنیم که به اهمیت $topic پی می‌بریم.
                
                داستان افرادی که این مسیر را طی کرده‌اند، داستان امید و تحول است. داستان کسانی که از ظلمت به نور رسیده‌اند.
            """.trimIndent()
            
            SpeechStage.ACTION -> """
                حال که متقاعد شدیم و احساس کردیم، وقت آن رسیده که عمل کنیم:
                
                گام اول: امروز همین الان تصمیم بگیرید که می‌خواهید تغییر کنید.
                گام دوم: یک برنامه مشخص برای خود تعریف کنید.
                گام سوم: با افراد موفق در این زمینه ارتباط برقرار کنید.
                
                به یاد داشته باشید، هر سفر بزرگی با یک قدم کوچک شروع می‌شود.
            """.trimIndent()
            
            SpeechStage.RAWZEH -> """
                ای کاش می‌دانستیم که چقدر وقت از دست داده‌ایم! ای کاش می‌فهمیدیم که هر لحظه‌ای که از دست می‌دهیم، فرصتی است که دیگر برنمی‌گردد.
                
                اما هنوز دیر نشده است. هنوز فرصت داریم. هنوز می‌توانیم تغییر کنیم.
                
                خدایا به ما کمک کن که در این راه استقامت کنیم. خدایا قلب‌های ما را روشن کن و راه را به ما نشان بده.
            """.trimIndent()
        }
    }
}