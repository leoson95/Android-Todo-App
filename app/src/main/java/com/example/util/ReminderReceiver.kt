package com.example.util

import com.example.R

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getIntExtra("taskId", -1)
        val taskTitle = intent.getStringExtra("taskTitle") ?: context.getString(com.example.R.string.reminder_notification_title)

        if (taskId == -1) return

        showNotification(context, taskId, taskTitle)
        SoundManager.playAlert()

        val pendingResult = goAsync()
        val db = AppDatabase.getDatabase(context)
        val dao = db.todoDao()
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = dao.getTaskById(taskId)
                if (task != null && !task.isCompleted && task.repeatType != null && task.repeatType != "none") {
                    val nextTime = calculateNextRepeatTime(task.reminderTime ?: System.currentTimeMillis(), task.repeatType)
                    if (nextTime != null) {
                        val updatedTask = task.copy(reminderTime = nextTime)
                        dao.updateTask(updatedTask)
                        ReminderScheduler.schedule(context, updatedTask)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun calculateNextRepeatTime(currentTime: Long, repeatType: String): Long? {
        val type = repeatType.substringBefore(':')
        val interval = when (type) {
            "daily" -> 24 * 60 * 60 * 1000L
            "every_other_day" -> {
                val days = repeatType.substringAfter(':', "2").toLongOrNull() ?: 2L
                days * 24 * 60 * 60 * 1000L
            }
            "weekly" -> 7 * 24 * 60 * 60 * 1000L
            else -> return null
        }
        return currentTime + interval
    }

    private fun showNotification(context: Context, taskId: Int, taskTitle: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "todo_reminders_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(com.example.R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(com.example.R.string.reminder_channel_desc)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val doneIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_DONE"
            putExtra("taskId", taskId)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId * 10 + 1,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = "ACTION_SNOOZE"
            putExtra("taskId", taskId)
            putExtra("taskTitle", taskTitle)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            taskId * 10 + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val appIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val appPendingIntent = PendingIntent.getActivity(
            context,
            taskId,
            appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(com.example.R.drawable.ic_launcher_foreground)
            .setContentTitle("⏰ " + context.getString(R.string.reminder_notification_title))
            .setContentText(taskTitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(appPendingIntent)
            .addAction(android.R.drawable.checkbox_on_background, context.getString(com.example.R.string.done), donePendingIntent)
            .addAction(android.R.drawable.ic_menu_recent_history, context.getString(com.example.R.string.snooze_10m), snoozePendingIntent)
            .build()

        notificationManager.notify(taskId, notification)
    }
}
