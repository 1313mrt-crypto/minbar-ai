package com.sokhanara.app.data.repository

import com.sokhanara.app.data.local.dao.SpeechDao
import com.sokhanara.app.data.mapper.SpeechMapper
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.repository.SpeechRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * پیاده‌سازی Speech Repository
 */
class SpeechRepositoryImpl @Inject constructor(
    private val speechDao: SpeechDao
) : SpeechRepository {
    
    override fun getAllSpeeches(): Flow<List<Speech>> {
        return speechDao.getAllSpeeches()
            .map { entities -> SpeechMapper.toDomainList(entities) }
    }
    
    override suspend fun getSpeechById(id: String): Speech? {
        return speechDao.getSpeechById(id)?.let { entity ->
            SpeechMapper.toDomain(entity)
        }
    }
    
    override fun getFavoriteSpeeches(): Flow<List<Speech>> {
        return speechDao.getFavoriteSpeeches()
            .map { entities -> SpeechMapper.toDomainList(entities) }
    }
    
    override fun searchSpeeches(query: String): Flow<List<Speech>> {
        return speechDao.searchSpeeches(query)
            .map { entities -> SpeechMapper.toDomainList(entities) }
    }
    
    override suspend fun saveSpeech(speech: Speech) {
        val entity = SpeechMapper.toEntity(speech)
        speechDao.insertSpeech(entity)
    }
    
    override suspend fun updateSpeech(speech: Speech) {
        val entity = SpeechMapper.toEntity(speech)
        speechDao.updateSpeech(entity)
    }
    
    override suspend fun deleteSpeech(id: String) {
        speechDao.deleteSpeechById(id)
    }
    
    override suspend fun toggleFavorite(id: String, isFavorite: Boolean) {
        speechDao.updateFavorite(id, isFavorite)
    }
}