package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "flashcards")
data class Flashcard(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val front: String,
    val back: String,
    val deckId: Long,
    
    // SM-2 parameters
    val interval: Int = 0, // In days
    val repetition: Int = 0,
    val easeFactor: Float = 2.5f,
    val nextReview: Long = System.currentTimeMillis()
)
