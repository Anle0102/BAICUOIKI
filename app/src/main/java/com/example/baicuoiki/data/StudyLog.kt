package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_logs")
data class StudyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long = 0, // 0 for session-level logs
    val quality: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
    val type: String = "CARD" // "CARD" or "SESSION"
)
