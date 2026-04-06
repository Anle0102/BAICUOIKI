package com.example.baicuoiki.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FlashcardRepository @Inject constructor(
    private val flashcardDao: FlashcardDao,
    private val deckDao: DeckDao,
    private val studyLogDao: StudyLogDao,
    private val studyScheduleDao: StudyScheduleDao
) {
    // --- Deck Operations ---
    fun getAllDecks(): Flow<List<Deck>> = deckDao.getAllDecks()
    
    suspend fun insertDeck(deck: Deck) = deckDao.insertDeck(deck)
    
    suspend fun updateDeck(deck: Deck) = deckDao.updateDeck(deck)
    
    suspend fun deleteDeck(deck: Deck) = deckDao.deleteDeck(deck)
    
    // --- Flashcard Operations ---
    fun getFlashcardsByDeck(deckId: Long): Flow<List<Flashcard>> = 
        flashcardDao.getFlashcardsByDeck(deckId)
        
    fun getFlashcardsToReview(currentTime: Long): Flow<List<Flashcard>> =
        flashcardDao.getFlashcardsToReview(currentTime)

    fun searchFlashcards(query: String): Flow<List<Flashcard>> =
        flashcardDao.searchFlashcards(query)
        
    suspend fun insertFlashcard(flashcard: Flashcard) = 
        flashcardDao.insertFlashcard(flashcard)
        
    suspend fun updateFlashcard(flashcard: Flashcard) = 
        flashcardDao.updateFlashcard(flashcard)
        
    suspend fun deleteFlashcard(flashcard: Flashcard) = 
        flashcardDao.deleteFlashcard(flashcard)

    // --- Study Log Operations ---
    suspend fun insertLog(log: StudyLog) = studyLogDao.insertLog(log)
    
    fun getLogCountSince(startTime: Long) = studyLogDao.getLogCountSince(startTime)

    // --- Schedule Operations ---
    fun getAllSchedules(): Flow<List<StudySchedule>> = studyScheduleDao.getAllSchedules()
    
    suspend fun insertSchedule(schedule: StudySchedule) = studyScheduleDao.insertSchedule(schedule)
    
    suspend fun deleteSchedule(schedule: StudySchedule) = studyScheduleDao.deleteSchedule(schedule)
}
