package com.sokhanara.app.data.local.dao

import androidx.room.*
import com.sokhanara.app.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

/**
 * Topic DAO
 */
@Dao
interface TopicDao {
    
    @Query("SELECT * FROM topics ORDER BY usageCount DESC")
    fun getAllTopics(): Flow<List<TopicEntity>>
    
    @Query("SELECT * FROM topics WHERE category = :category ORDER BY usageCount DESC")
    fun getTopicsByCategory(category: String): Flow<List<TopicEntity>>
    
    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): TopicEntity?
    
    @Query("SELECT * FROM topics WHERE title LIKE '%' || :query || '%' OR keywords LIKE '%' || :query || '%'")
    fun searchTopics(query: String): Flow<List<TopicEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)
    
    @Query("UPDATE topics SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: String)
}