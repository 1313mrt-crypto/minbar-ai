package com.sokhanara.app.data.local.dao

import androidx.room.*
import com.sokhanara.app.data.local.entity.EmotionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Emotion DAO
 */
@Dao
interface EmotionDao {
    
    @Query("SELECT * FROM emotion_analysis WHERE speechId = :speechId ORDER BY analyzedAt DESC")
    fun getEmotionsBySpeech(speechId: String): Flow<List<EmotionEntity>>
    
    @Query("SELECT * FROM emotion_analysis WHERE speechId = :speechId ORDER BY analyzedAt DESC LIMIT 1")
    suspend fun getLatestEmotionBySpeech(speechId: String): EmotionEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmotion(emotion: EmotionEntity)
    
    @Delete
    suspend fun deleteEmotion(emotion: EmotionEntity)
}