package com.sokhanara.app.ui.screens.preview

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokhanara.app.domain.model.ExportFormat
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.usecase.export.*
import com.sokhanara.app.domain.usecase.speech.GetSpeechByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Preview ViewModel
 */
@HiltViewModel
class PreviewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getSpeechByIdUseCase: GetSpeechByIdUseCase,
    private val generatePowerPointUseCase: GeneratePowerPointUseCase,
    private val generateInfographicUseCase: GenerateInfographicUseCase,
    private val generateChecklistUseCase: GenerateChecklistUseCase
) : ViewModel() {
    
    private val speechId: String = savedStateHandle["projectId"] ?: ""
    
    private val _uiState = MutableStateFlow<PreviewUiState>(PreviewUiState.Loading)
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()
    
    init {
        loadSpeech()
    }
    
    private fun loadSpeech() {
        viewModelScope.launch {
            try {
                val result = getSpeechByIdUseCase(speechId)
                
                result.fold(
                    onSuccess = { speech ->
                        _uiState.value = PreviewUiState.Success(speech)
                    },
                    onFailure = { error ->
                        _uiState.value = PreviewUiState.Error(
                            error.message ?: "سخنرانی یافت نشد"
                        )
                    }
                )
            } catch (e: Exception) {
                Timber.e(e, "Error loading speech")
                _uiState.value = PreviewUiState.Error(e.message ?: "خطای نامشخص")
            }
        }
    }
    
    fun exportToFormat(format: ExportFormat) {
        val currentState = _uiState.value
        if (currentState !is PreviewUiState.Success) return
        
        viewModelScope.launch {
            _uiState.value = PreviewUiState.Exporting(format)
            
            try {
                val result = when (format) {
                    ExportFormat.PPTX -> generatePowerPointUseCase(currentState.speech)
                    ExportFormat.PNG -> generateInfographicUseCase(currentState.speech)
                    ExportFormat.PDF -> generateChecklistUseCase(currentState.speech)
                    else -> Result.failure(Exception("فرمت پشتیبانی نمی‌شود"))
                }
                
                result.fold(
                    onSuccess = { filePath ->
                        _uiState.value = PreviewUiState.ExportSuccess(
                            speech = currentState.speech,
                            filePath = filePath
                        )
                        Timber.d("Export successful: $filePath")
                    },
                    onFailure = { error ->
                        _uiState.value = PreviewUiState.Error(
                            error.message ?: "خطا در تولید فایل"
                        )
                    }
                )
                
            } catch (e: Exception) {
                Timber.e(e, "Error exporting")
                _uiState.value = PreviewUiState.Error(e.message ?: "خطای نامشخص")
            }
        }
    }
    
    fun resetExportState(speech: Speech) {
        _uiState.value = PreviewUiState.Success(speech)
    }
}

/**
 * Preview UI State
 */
sealed class PreviewUiState {
    object Loading : PreviewUiState()
    data class Success(val speech: Speech) : PreviewUiState()
    data class Exporting(val format: ExportFormat) : PreviewUiState()
    data class ExportSuccess(val speech: Speech, val filePath: String) : PreviewUiState()
    data class Error(val message: String) : PreviewUiState()
}
