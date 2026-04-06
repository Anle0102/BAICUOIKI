package com.example.baicuoiki.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyLogDao {
    @Insert
    suspend fun insertLog(log: StudyLog)

    @Query("SELECT * FROM study_logs")
    fun getAllLogs(): Flow<List<StudyLog>>

    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :startTime")
    fun getLogCountSince(startTime: Long): Flow<Int>

    // Lấy số lượng thẻ đã học trong 7 ngày gần nhất, nhóm theo ngày
    @Query("SELECT COUNT(*) FROM study_logs WHERE timestamp >= :startTime GROUP BY (timestamp / 86400000)")
    fun getDailyStats(startTime: Long): Flow<List<Int>>
}
