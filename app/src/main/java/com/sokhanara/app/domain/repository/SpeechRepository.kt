package com.sokhanara.app.domain.repository

import com.sokhanara.app.domain.model.Speech
import kotlinx.coroutines.flow.Flow

/**
 * Speech Repository Interface
 */
interface SpeechRepository {
    
    fun getAllSpeeches(): Flow<List<Speech>>
    
    suspend fun getSpeechById(id: String): Speech?
    
    fun getFavoriteSpeeches(): Flow<List<Speech>>
    
    fun searchSpeeches(query: String): Flow<List<Speech>>
    
    suspend fun saveSpeech(speech: Speech)
    
    suspend fun updateSpeech(speech: Speech)
    
    suspend fun deleteSpeech(id: String)
    
    suspend fun toggleFavorite(id: String, isFavorite: Boolean)
}