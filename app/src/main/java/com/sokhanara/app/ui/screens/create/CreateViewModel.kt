package com.sokhanara.app.ui.screens.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokhanara.app.ai.engine.AiEngine
import com.sokhanara.app.domain.model.*
import com.sokhanara.app.domain.usecase.source.ParseSourceUseCase
import com.sokhanara.app.domain.usecase.speech.GenerateSpeechUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Create Speech ViewModel
 */
@HiltViewModel
class CreateViewModel @Inject constructor(
    private val generateSpeechUseCase: GenerateSpeechUseCase,
    private val parseSourceUseCase: ParseSourceUseCase,
    private val aiEngine: AiEngine
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CreateUiState>(CreateUiState.Idle)
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()
    
    private val _sources = MutableStateFlow<List<Source>>(emptyList())
    val sources: StateFlow<List<Source>> = _sources.asStateFlow()
    
    fun addSource(uri: String, type: SourceType) {
        viewModelScope.launch {
            _uiState.value = CreateUiState.ParsingSource
            
            parseSourceUseCase(uri, type)
                .onSuccess { source ->
                    _sources.value = _sources.value + source
                    _uiState.value = CreateUiState.SourceAdded
                    Timber.d("Source added successfully")
                }
                .onFailure { error ->
                    _uiState.value = CreateUiState.Error(
                        error.message ?: "خطا در پردازش منبع"
                    )
                    Timber.e(error, "Error parsing source")
                }
        }
    }
    
    fun removeSource(source: Source) {
        _sources.value = _sources.value.filter { it.id != source.id }
    }
    
    fun generateSpeech(
        title: String,
        topic: String?,
        language: Language,
        style: SpeechStyle,
        theme: VisualTheme
    ) {
        if (_sources.value.isEmpty()) {
            _uiState.value = CreateUiState.Error("حداقل یک منبع اضافه کنید")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = CreateUiState.Generating(0)
            
            try {
                // ایجاد سخنرانی اولیه
                val speechResult = generateSpeechUseCase(
                    title = title,
                    topic = topic,
                    sources = _sources.value,
                    language = language,
                    style = style,
                    theme = theme
                )
                
                speechResult.fold(
                    onSuccess = { speech ->
                        // تولید محتوا با AI
                        generateContent(speech, language, style)
                    },
                    onFailure = { error ->
                        _uiState.value = CreateUiState.Error(
                            error.message ?: "خطا در ایجاد سخنرانی"
                        )
                    }
                )
                
            } catch (e: Exception) {
                Timber.e(e, "Error generating speech")
                _uiState.value = CreateUiState.Error(e.message ?: "خطای نامشخص")
            }
        }
    }
    
    private suspend fun generateContent(
        speech: Speech,
        language: Language,
        style: SpeechStyle
    ) {
        _uiState.value = CreateUiState.Generating(20)
        
        val result = aiEngine.generateFullSpeech(
            topic = speech.title,
            sources = speech.sources,
            style = style,
            language = language
        )
        
        result.fold(
            onSuccess = { stages ->
                val completedSpeech = speech.copy(stages = stages)
                _uiState.value = CreateUiState.Success(completedSpeech)
                Timber.d("Speech generated successfully")
            },
            onFailure = { error ->
                _uiState.value = CreateUiState.Error(
                    error.message ?: "خطا در تولید محتوا"
                )
                Timber.e(error, "Error generating content")
            }
        )
    }
    
    fun resetState() {
        _uiState.value = CreateUiState.Idle
    }
}

/**
 * Create UI State
 */
sealed class CreateUiState {
    object Idle : CreateUiState()
    object ParsingSource : CreateUiState()
    object SourceAdded : CreateUiState()
    data class Generating(val progress: Int) : CreateUiState()
    data class Success(val speech: Speech) : CreateUiState()
    data class Error(val message: String) : CreateUiState()
}
