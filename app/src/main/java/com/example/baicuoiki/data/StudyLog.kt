package com.example.baicuoiki.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "study_logs")
data class StudyLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cardId: Long,
    val quality: Int,
    val timestamp: Long = System.currentTimeMillis()
)
