package com.sokhanara.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sokhanara.app.data.local.dao.*
import com.sokhanara.app.data.local.entity.*

/**
 * App Database
 * پایگاه داده اصلی برنامه
 */
@Database(
    entities = [
        SpeechEntity::class,
        ProjectEntity::class,
        TopicEntity::class,
        EmotionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun speechDao(): SpeechDao
    abstract fun projectDao(): ProjectDao
    abstract fun topicDao(): TopicDao
    abstract fun emotionDao(): EmotionDao
}