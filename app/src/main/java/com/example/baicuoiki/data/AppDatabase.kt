package com.example.baicuoiki.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Flashcard::class, Deck::class, User::class, StudyLog::class, StudySchedule::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun flashcardDao(): FlashcardDao
    abstract fun deckDao(): DeckDao
    abstract fun userDao(): UserDao
    abstract fun studyLogDao(): StudyLogDao
    abstract fun studyScheduleDao(): StudyScheduleDao
}
