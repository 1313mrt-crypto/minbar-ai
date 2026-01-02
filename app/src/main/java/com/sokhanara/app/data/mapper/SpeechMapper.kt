package com.sokhanara.app.data.mapper

import com.sokhanara.app.data.local.entity.SpeechEntity
import com.sokhanara.app.domain.model.*

/**
 * تبدیل بین Entity و Domain Model
 */
object SpeechMapper {
    
    fun toEntity(speech: Speech): SpeechEntity {
        return SpeechEntity(
            id = speech.id,
            title = speech.title,
            topic = speech.topic,
            language = speech.language.code,
            style = speech.style.code,
            theme = speech.theme.code,
            stageMotivation = speech.stages[SpeechStage.MOTIVATION],
            stageConviction = speech.stages[SpeechStage.CONVICTION],
            stageEmotion = speech.stages[SpeechStage.EMOTION],
            stageAction = speech.stages[SpeechStage.ACTION],
            stageRawzeh = speech.stages[SpeechStage.RAWZEH],
            sources = speech.sources.map { it.content },
            pdfPath = speech.outputs.pdfPath,
            pptxPath = speech.outputs.pptxPath,
            infographicPath = speech.outputs.infographicPath,
            audioPath = speech.outputs.audioPath,
            checklistPath = speech.outputs.checklistPath,
            createdAt = speech.metadata.createdAt,
            updatedAt = speech.metadata.updatedAt,
            isFavorite = speech.metadata.isFavorite,
            estimatedDuration = speech.metadata.estimatedDuration
        )
    }
    
    fun toDomain(entity: SpeechEntity): Speech {
        val stages = mutableMapOf<SpeechStage, String>()
        entity.stageMotivation?.let { stages[SpeechStage.MOTIVATION] = it }
        entity.stageConviction?.let { stages[SpeechStage.CONVICTION] = it }
        entity.stageEmotion?.let { stages[SpeechStage.EMOTION] = it }
        entity.stageAction?.let { stages[SpeechStage.ACTION] = it }
        entity.stageRawzeh?.let { stages[SpeechStage.RAWZEH] = it }
        
        return Speech(
            id = entity.id,
            title = entity.title,
            topic = entity.topic,
            language = Language.fromCode(entity.language),
            style = SpeechStyle.fromCode(entity.style),
            theme = VisualTheme.fromCode(entity.theme),
            stages = stages,
            sources = entity.sources.map { content ->
                Source(
                    id = "source_${System.currentTimeMillis()}",
                    type = SourceType.TXT,
                    content = content,
                    title = null,
                    metadata = null
                )
            },
            outputs = SpeechOutputs(
                pdfPath = entity.pdfPath,
                pptxPath = entity.pptxPath,
                infographicPath = entity.infographicPath,
                audioPath = entity.audioPath,
                checklistPath = entity.checklistPath
            ),
            metadata = SpeechMetadata(
                createdAt = entity.createdAt,
                updatedAt = entity.updatedAt,
                isFavorite = entity.isFavorite,
                estimatedDuration = entity.estimatedDuration
            )
        )
    }
    
    fun toDomainList(entities: List<SpeechEntity>): List<Speech> {
        return entities.map { toDomain(it) }
    }
}