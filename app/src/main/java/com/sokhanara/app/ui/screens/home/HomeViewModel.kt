package com.sokhanara.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sokhanara.app.domain.model.Speech
import com.sokhanara.app.domain.usecase.speech.GetAllSpeechesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Home ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllSpeechesUseCase: GetAllSpeechesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadSpeeches()
    }
    
    private fun loadSpeeches() {
        viewModelScope.launch {
            try {
                getAllSpeechesUseCase()
                    .catch { error ->
                        Timber.e(error, "Error loading speeches")
                        _uiState.value = HomeUiState.Error(
                            error.message ?: "خطا در بارگذاری سخنرانی‌ها"
                        )
                    }
                    .collect { speeches ->
                        _uiState.value = if (speeches.isEmpty()) {
                            HomeUiState.Empty
                        } else {
                            HomeUiState.Success(speeches)
                        }
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error in loadSpeeches")
                _uiState.value = HomeUiState.Error(e.message ?: "خطای نامشخص")
            }
        }
    }
    
    fun refresh() {
        loadSpeeches()
    }
}

/**
 * Home UI State
 */
sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val speeches: List<Speech>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
