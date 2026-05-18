package com.pokemontcg.collector.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokemontcg.collector.data.repository.CardRepository
import com.pokemontcg.collector.domain.model.Card
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CollectionUiState {
    data object Loading : CollectionUiState
    data class Success(val cards: List<Card>, val totalCount: Int) : CollectionUiState
    data class Error(val message: String) : CollectionUiState
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class CollectionViewModel @Inject constructor(
    private val cardRepository: CardRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<CollectionUiState>(CollectionUiState.Loading)
    val uiState: StateFlow<CollectionUiState> = _uiState.asStateFlow()

    init {
        observeCards()
    }

    private fun observeCards() {
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(300),
                cardRepository.getCardCount()
            ) { query, count ->
                Pair(query, count)
            }.flatMapLatest { (query, count) ->
                val cardsFlow = if (query.isBlank()) {
                    cardRepository.getCollectionCards()
                } else {
                    cardRepository.searchCollectionCards(query)
                }
                cardsFlow.map { cards -> Pair(cards, count) }
            }.catch { e ->
                _uiState.value = CollectionUiState.Error(e.message ?: "Unknown error")
            }.collect { (cards, count) ->
                _uiState.value = CollectionUiState.Success(cards, count)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun removeCard(card: Card) {
        viewModelScope.launch {
            cardRepository.removeFromCollection(card)
        }
    }

    fun toggleWishlist(card: Card) {
        viewModelScope.launch {
            cardRepository.updateWishlist(card.id, !card.inWishlist)
        }
    }
}
