package com.sokhanara.app.data.repository

import com.sokhanara.app.data.local.dao.TopicDao
import com.sokhanara.app.data.mapper.TopicMapper
import com.sokhanara.app.domain.model.Topic
import com.sokhanara.app.domain.model.TopicCategory
import com.sokhanara.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * پیاده‌سازی Library Repository
 */
class LibraryRepositoryImpl @Inject constructor(
    private val topicDao: TopicDao
) : LibraryRepository {
    
    override fun getAllTopics(): Flow<List<Topic>> {
        return topicDao.getAllTopics()
            .map { entities -> TopicMapper.toDomainList(entities) }
    }
    
    override fun getTopicsByCategory(category: TopicCategory): Flow<List<Topic>> {
        return topicDao.getTopicsByCategory(category.name.lowercase())
            .map { entities -> TopicMapper.toDomainList(entities) }
    }
    
    override fun searchTopics(query: String): Flow<List<Topic>> {
        return topicDao.searchTopics(query)
            .map { entities -> TopicMapper.toDomainList(entities) }
    }
    
    override suspend fun getTopicById(id: String): Topic? {
        return topicDao.getTopicById(id)?.let { entity ->
            TopicMapper.toDomain(entity)
        }
    }
    
    override suspend fun suggestSources(topicId: String): List<String> {
        val topic = topicDao.getTopicById(topicId)
        return topic?.sources ?: emptyList()
    }
    
    override suspend fun incrementTopicUsage(topicId: String) {
        topicDao.incrementUsageCount(topicId)
    }
}