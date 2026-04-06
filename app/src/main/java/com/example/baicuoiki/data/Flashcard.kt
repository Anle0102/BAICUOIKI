package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val front: String,
    val back: String,
    val hint: String = "",
    val deckId: Long,
    
    // SM-2 parameters
    val interval: Int = 0,
    val repetition: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReview: Long = System.currentTimeMillis()
)
