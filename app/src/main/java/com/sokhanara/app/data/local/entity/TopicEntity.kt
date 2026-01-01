package com.sokhanara.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sokhanara.app.data.local.database.Converters

/**
 * Topic Entity
 * جدول موضوعات (کتابخانه)
 */
@Entity(tableName = "topics")
@TypeConverters(Converters::class)
data class TopicEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val titleArabic: String?,
    val category: String, // quran, hadith, imam, akhlaq
    val description: String?,
    val sources: List<String>, // منابع پیشنهادی
    val keywords: List<String>,
    val usageCount: Int = 0
)