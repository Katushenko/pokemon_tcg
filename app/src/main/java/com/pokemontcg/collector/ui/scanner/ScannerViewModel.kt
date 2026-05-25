package com.pokemontcg.collector.ui.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemontcg.collector.data.repository.CardRepository
import com.pokemontcg.collector.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ScannerUiState {
    data object Scanning : ScannerUiState
    data object Searching : ScannerUiState
    data class Results(val cards: List<Card>, val query: String) : ScannerUiState
    data class Added(val card: Card) : ScannerUiState
    data class Error(val message: String) : ScannerUiState
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.Scanning)
    val uiState: StateFlow<ScannerUiState> = _uiState.asStateFlow()

    private val _manualQuery = MutableStateFlow("")
    val manualQuery: StateFlow<String> = _manualQuery.asStateFlow()

    fun onOcrResult(recognizedText: String) {
        val query = buildQueryFromOcr(recognizedText)
        if (query.isNotBlank()) {
            searchCards(query)
        }
    }

    fun onManualQueryChange(query: String) {
        _manualQuery.value = query
    }

    fun searchManually() {
        val query = _manualQuery.value.trim()
        if (query.isNotBlank()) {
            searchCards("name:*$query*")
        }
    }

    private fun searchCards(query: String) {
        viewModelScope.launch {
            _uiState.value = ScannerUiState.Searching
            runCatching { cardRepository.searchApiCards(query) }
                .onSuccess { cards ->
                    _uiState.value = if (cards.isEmpty()) {
                        ScannerUiState.Error("No cards found for: $query")
                    } else {
                        ScannerUiState.Results(cards, query)
                    }
                }
                .onFailure { e ->
                    _uiState.value = ScannerUiState.Error(e.message ?: "Search failed")
                }
        }
    }

    fun addToCollection(card: Card) {
        viewModelScope.launch {
            runCatching { cardRepository.addToCollection(card) }
                .onSuccess { _uiState.value = ScannerUiState.Added(card) }
                .onFailure { e ->
                    _uiState.value = ScannerUiState.Error(e.message ?: "Failed to add card")
                }
        }
    }

    fun resetToScanning() {
        _uiState.value = ScannerUiState.Scanning
        _manualQuery.value = ""
    }

    private fun buildQueryFromOcr(text: String): String {
        val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
        val numberRegex = Regex("""(\d+)/(\d+)""")
        val numberMatch = lines.firstNotNullOfOrNull { numberRegex.find(it) }
        return if (numberMatch != null) {
            "number:${numberMatch.groupValues[1]}"
        } else {
            val nameLine = lines.firstOrNull { it.length in 3..30 && it[0].isUpperCase() }
            if (nameLine != null) "name:*$nameLine*" else ""
        }
    }
}
