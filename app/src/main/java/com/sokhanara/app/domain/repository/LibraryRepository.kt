package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.Topic
import com.sokhanara.app.domain.model.TopicCategory
import kotlinx.coroutines.flow.Flow

/**
 * Library Repository Interface
 */
interface LibraryRepository {
    
    fun getAllTopics(): Flow<List<Topic>>
    
    fun getTopicsByCategory(category: TopicCategory): Flow<List<Topic>>
    
    fun searchTopics(query: String): Flow<List<Topic>>
    
    suspend fun getTopicById(id: String): Topic?
    
    suspend fun suggestSources(topicId: String): List<String>
    
    suspend fun incrementTopicUsage(topicId: String)
}