package com.example.baicuoiki.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.baicuoiki.MainActivity

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val deckName = inputData.getString("deck_name")
        val isSpecific = inputData.getBoolean("is_specific", false)
        
        showNotification(deckName, isSpecific)
        return Result.success()
    }

    private fun showNotification(deckName: String?, isSpecific: Boolean) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        // Sử dụng Channel ID mới để đảm bảo mức ưu tiên cao nhất
        val channelId = if (isSpecific) "study_schedule_urgent" else "study_reminder_daily"
        val channelName = if (isSpecific) "Lịch học cụ thể" else "Nhắc nhở hàng ngày"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = if (isSpecific) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Kênh thông báo nhắc học bài"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val title = if (isSpecific) "Đã đến giờ học bài!" else "Bạn ơi, vào ôn tập nhé!"
        val content = if (isSpecific && deckName != null) 
            "Bắt đầu học bộ thẻ: $deckName ngay thôi nào." 
            else "Đừng quên dành 5 phút để ôn tập hôm nay nhé."

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 
            System.currentTimeMillis().toInt(), // ID duy nhất cho Intent
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(if (isSpecific) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Rung và chuông
            .build()

        // Sử dụng ID ngẫu nhiên để các thông báo không đè lên nhau
        val notificationId = if (isSpecific) System.currentTimeMillis().toInt() else 1
        notificationManager.notify(notificationId, notification)
    }
}
