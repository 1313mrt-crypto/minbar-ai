package com.sokhanara.app.data.mapper

import com.sokhanara.app.data.local.entity.TopicEntity
import com.sokhanara.app.domain.model.Topic
import com.sokhanara.app.domain.model.TopicCategory

/**
 * تبدیل Topic Entity ↔ Domain
 */
object TopicMapper {
    
    fun toEntity(topic: Topic): TopicEntity {
        return TopicEntity(
            id = topic.id,
            title = topic.title,
            titleArabic = topic.titleArabic,
            category = topic.category.name.lowercase(),
            description = topic.description,
            sources = topic.sources,
            keywords = topic.keywords,
            usageCount = topic.usageCount
        )
    }
    
    fun toDomain(entity: TopicEntity): Topic {
        return Topic(
            id = entity.id,
            title = entity.title,
            titleArabic = entity.titleArabic,
            category = TopicCategory.valueOf(entity.category.uppercase()),
            description = entity.description,
            sources = entity.sources,
            keywords = entity.keywords,
            usageCount = entity.usageCount
        )
    }
    
    fun toDomainList(entities: List<TopicEntity>): List<Topic> {
        return entities.map { toDomain(it) }
    }
}