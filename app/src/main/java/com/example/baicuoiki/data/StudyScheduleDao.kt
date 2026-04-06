package com.example.baicuoiki.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyScheduleDao {
    @Query("SELECT * FROM study_schedules ORDER BY scheduledTime ASC")
    fun getAllSchedules(): Flow<List<StudySchedule>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: StudySchedule)

    @Delete
    suspend fun deleteSchedule(schedule: StudySchedule)

    @Query("SELECT * FROM study_schedules WHERE scheduledTime > :currentTime AND isNotified = 0 ORDER BY scheduledTime ASC LIMIT 1")
    suspend fun getNextPendingSchedule(currentTime: Long): StudySchedule?

    @Update
    suspend fun updateSchedule(schedule: StudySchedule)
}
