package com.pokemontcg.collector.ui.carddetail

import androidx.lifecycle.SavedStateHandle
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

sealed interface CardDetailUiState {
    data object Loading : CardDetailUiState
    data class Success(val card: Card) : CardDetailUiState
    data class Error(val message: String) : CardDetailUiState
}

@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cardId: Long = checkNotNull(savedStateHandle["cardId"])

    private val _uiState = MutableStateFlow<CardDetailUiState>(CardDetailUiState.Loading)
    val uiState: StateFlow<CardDetailUiState> = _uiState.asStateFlow()

    init {
        loadCard()
    }

    private fun loadCard() {
        viewModelScope.launch {
            val card = cardRepository.getCardById(cardId)
            _uiState.value = if (card != null) {
                CardDetailUiState.Success(card)
            } else {
                CardDetailUiState.Error("Card not found")
            }
        }
    }

    fun updateNotes(notes: String) {
        viewModelScope.launch {
            cardRepository.updateNotes(cardId, notes)
            loadCard()
        }
    }

    fun toggleWishlist() {
        val current = (_uiState.value as? CardDetailUiState.Success)?.card ?: return
        viewModelScope.launch {
            cardRepository.updateWishlist(cardId, !current.inWishlist)
            loadCard()
        }
    }

    fun toggleForTrade() {
        val current = (_uiState.value as? CardDetailUiState.Success)?.card ?: return
        viewModelScope.launch {
            cardRepository.updateForTrade(cardId, !current.forTrade)
            loadCard()
        }
    }

    fun removeFromCollection(onRemoved: () -> Unit) {
        val current = (_uiState.value as? CardDetailUiState.Success)?.card ?: return
        viewModelScope.launch {
            cardRepository.removeFromCollection(current)
            onRemoved()
        }
    }
}
