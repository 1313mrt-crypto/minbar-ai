package com.sokhanara.app.data.local.dao

import androidx.room.*
import com.sokhanara.app.data.local.entity.SpeechEntity
import kotlinx.coroutines.flow.Flow

/**
 * Speech DAO
 * عملیات پایگاه داده برای سخنرانی‌ها
 */
@Dao
interface SpeechDao {
    
    @Query("SELECT * FROM speeches ORDER BY createdAt DESC")
    fun getAllSpeeches(): Flow<List<SpeechEntity>>
    
    @Query("SELECT * FROM speeches WHERE id = :id")
    suspend fun getSpeechById(id: String): SpeechEntity?
    
    @Query("SELECT * FROM speeches WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteSpeeches(): Flow<List<SpeechEntity>>
    
    @Query("SELECT * FROM speeches WHERE topic LIKE '%' || :query || '%' OR title LIKE '%' || :query || '%'")
    fun searchSpeeches(query: String): Flow<List<SpeechEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpeech(speech: SpeechEntity)
    
    @Update
    suspend fun updateSpeech(speech: SpeechEntity)
    
    @Delete
    suspend fun deleteSpeech(speech: SpeechEntity)
    
    @Query("DELETE FROM speeches WHERE id = :id")
    suspend fun deleteSpeechById(id: String)
    
    @Query("UPDATE speeches SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}