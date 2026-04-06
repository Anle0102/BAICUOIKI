package com.example.baicuoiki.worker

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val DAILY_REMINDER_NAME = "study_reminder_work"

    // Nhắc nhở hàng ngày (giữ nguyên)
    fun scheduleDailyReminder(context: Context, hour: Int = 9, minute: Int = 0) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val initialDelay = calculateInitialDelay(hour, minute)

        val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag(DAILY_REMINDER_NAME)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_REMINDER_NAME,
            ExistingPeriodicWorkPolicy.REPLACE,
            dailyWorkRequest
        )
    }

    // HẸN GIỜ CHO MỘT BUỔI HỌC CỤ THỂ (MỚI)
    fun scheduleSpecificStudySession(context: Context, scheduleId: Long, deckName: String, timeMillis: Long) {
        val delay = timeMillis - System.currentTimeMillis()
        if (delay <= 0) return

        val data = workDataOf(
            "deck_name" to deckName,
            "is_specific" to true
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag("schedule_$scheduleId")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "study_session_$scheduleId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun calculateInitialDelay(hour: Int, minute: Int): Long {
        val calendar = java.util.Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        calendar.set(java.util.Calendar.SECOND, 0)
        
        if (calendar.timeInMillis <= currentTime) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis - currentTime
    }
}
