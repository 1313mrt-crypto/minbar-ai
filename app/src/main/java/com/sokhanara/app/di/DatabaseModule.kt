package com.sokhanara.app.di

import android.content.Context
import androidx.room.Room
import com.sokhanara.app.data.local.dao.*
import com.sokhanara.app.data.local.database.AppDatabase
import com.sokhanara.app.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Database Module
 * تنظیمات پایگاه داده
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideSpeechDao(database: AppDatabase): SpeechDao {
        return database.speechDao()
    }
    
    @Provides
    @Singleton
    fun provideProjectDao(database: AppDatabase): ProjectDao {
        return database.projectDao()
    }
    
    @Provides
    @Singleton
    fun provideTopicDao(database: AppDatabase): TopicDao {
        return database.topicDao()
    }
    
    @Provides
    @Singleton
    fun provideEmotionDao(database: AppDatabase): EmotionDao {
        return database.emotionDao()
    }
}