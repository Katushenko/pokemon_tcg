package com.pokemontcg.collector.ui.sets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemontcg.collector.data.repository.SetRepository
import com.pokemontcg.collector.domain.model.PokemonSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SetsUiState {
    data object Loading : SetsUiState
    data class Success(val sets: List<PokemonSet>) : SetsUiState
    data class Error(val message: String) : SetsUiState
}

@HiltViewModel
class SetsViewModel @Inject constructor(
    private val setRepository: SetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SetsUiState>(SetsUiState.Loading)
    val uiState: StateFlow<SetsUiState> = _uiState.asStateFlow()

    init {
        loadSets()
    }

    private fun loadSets() {
        viewModelScope.launch {
            setRepository.getLocalSets()
                .catch { e -> _uiState.value = SetsUiState.Error(e.message ?: "Error") }
                .collect { sets ->
                    if (sets.isEmpty()) {
                        fetchRemoteSets()
                    } else {
                        _uiState.value = SetsUiState.Success(sets)
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = SetsUiState.Loading
            runCatching { setRepository.fetchAndCacheSets() }
                .onSuccess { sets -> _uiState.value = SetsUiState.Success(sets) }
                .onFailure { e -> _uiState.value = SetsUiState.Error(e.message ?: "Fetch failed") }
        }
    }

    private suspend fun fetchRemoteSets() {
        runCatching { setRepository.fetchAndCacheSets() }
            .onSuccess { sets -> _uiState.value = SetsUiState.Success(sets) }
            .onFailure { e -> _uiState.value = SetsUiState.Error(e.message ?: "Fetch failed") }
    }
}
