package com.example.baicuoiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baicuoiki.data.Flashcard
import com.example.baicuoiki.data.FlashcardRepository
import com.example.baicuoiki.util.SM2Algorithm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReviewUiState>(ReviewUiState.Loading)
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var reviewList = listOf<Flashcard>()
    private var currentIndex = 0

    init {
        loadFlashcards()
    }

    private fun loadFlashcards() {
        viewModelScope.launch {
            val cards = repository.getFlashcardsToReview(System.currentTimeMillis()).first()
            reviewList = cards
            if (reviewList.isEmpty()) {
                _uiState.value = ReviewUiState.Empty
            } else {
                updateCurrentCard()
            }
        }
    }

    private fun updateCurrentCard() {
        if (currentIndex < reviewList.size) {
            _uiState.value = ReviewUiState.Success(reviewList[currentIndex], isFlipped = false)
        } else {
            _uiState.value = ReviewUiState.Finished
        }
    }

    fun flipCard() {
        val current = _uiState.value
        if (current is ReviewUiState.Success) {
            _uiState.value = current.copy(isFlipped = !current.isFlipped)
        }
    }

    fun rateCard(quality: Int) {
        val current = _uiState.value
        if (current is ReviewUiState.Success) {
            viewModelScope.launch {
                val updatedResult = SM2Algorithm.calculate(current.card, quality)
                val updatedCard = current.card.copy(
                    interval = updatedResult.interval,
                    repetition = updatedResult.repetition,
                    easeFactor = updatedResult.easeFactor,
                    nextReview = updatedResult.nextReview
                )
                repository.updateFlashcard(updatedCard)
                currentIndex++
                updateCurrentCard()
            }
        }
    }
}

sealed class ReviewUiState {
    object Loading : ReviewUiState()
    object Empty : ReviewUiState()
    data class Success(val card: Flashcard, val isFlipped: Boolean) : ReviewUiState()
    object Finished : ReviewUiState()
}
