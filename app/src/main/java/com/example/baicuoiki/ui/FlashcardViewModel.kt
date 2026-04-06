package com.example.baicuoiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baicuoiki.data.*
import com.example.baicuoiki.util.SM2Algorithm
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashcardViewModel @Inject constructor(
    private val repository: FlashcardRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val decks: StateFlow<List<Deck>> = repository.getAllDecks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<StudySchedule>> = repository.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getFlashcardsForDeck(deckId: Long): Flow<List<Flashcard>> {
        return repository.getFlashcardsByDeck(deckId)
    }

    fun getCardsToReviewToday(): Flow<List<Flashcard>> {
        return repository.getFlashcardsToReview(System.currentTimeMillis())
    }

    fun searchCards(query: String): Flow<List<Flashcard>> {
        return repository.searchFlashcards(query)
    }

    fun getStudyCountPastWeek(): Flow<Int> {
        val oneWeekAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000L)
        return repository.getLogCountSince(oneWeekAgo)
    }

    // --- Export Logic ---
    fun exportDeckToJson(deck: Deck, cards: List<Flashcard>): String {
        val exportData = DeckExport(
            deckName = deck.name,
            description = deck.description,
            cards = cards.map { CardExport(it.front, it.back) }
        )
        return Gson().toJson(exportData)
    }

    // --- Deck CRUD ---
    fun addDeck(name: String, description: String = "") {
        viewModelScope.launch {
            repository.insertDeck(Deck(name = name, description = description))
        }
    }

    fun updateDeck(deck: Deck) {
        viewModelScope.launch {
            repository.updateDeck(deck)
        }
    }

    fun deleteDeck(deck: Deck) {
        viewModelScope.launch {
            repository.deleteDeck(deck)
        }
    }

    // --- Flashcard CRUD ---
    fun addFlashcard(deckId: Long, front: String, back: String, hint: String = "") {
        viewModelScope.launch {
            repository.insertFlashcard(Flashcard(deckId = deckId, front = front, back = back, hint = hint))
        }
    }

    fun updateFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.updateFlashcard(flashcard)
        }
    }

    fun deleteFlashcard(flashcard: Flashcard) {
        viewModelScope.launch {
            repository.deleteFlashcard(flashcard)
        }
    }

    fun updateFlashcardReview(card: Flashcard, quality: Int) {
        viewModelScope.launch {
            val result = SM2Algorithm.calculate(card, quality)
            val updatedCard = card.copy(
                interval = result.interval,
                repetition = result.repetition,
                easeFactor = result.easeFactor,
                nextReview = result.nextReview
            )
            repository.updateFlashcard(updatedCard)
            repository.insertLog(StudyLog(cardId = card.id, quality = quality))
        }
    }

    // --- Schedule Operations ---
    fun addSchedule(deckId: Long, deckName: String, timeMillis: Long) {
        viewModelScope.launch {
            repository.insertSchedule(StudySchedule(deckId = deckId, deckName = deckName, scheduledTime = timeMillis))
        }
    }

    fun deleteSchedule(schedule: StudySchedule) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }
}
