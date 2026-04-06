package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_schedules")
data class StudySchedule(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: Long,
    val deckName: String,
    val scheduledTime: Long, // Epoch milliseconds
    val isNotified: Boolean = false
)
