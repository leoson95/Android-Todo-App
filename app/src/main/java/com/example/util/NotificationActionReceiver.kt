package com.example.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val taskId = intent.getIntExtra("taskId", -1)
        if (taskId == -1) return

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(taskId)

        val pendingResult = goAsync()
        val db = AppDatabase.getDatabase(context)
        val dao = db.todoDao()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = dao.getTaskById(taskId)
                if (task != null) {
                    when (action) {
                        "ACTION_DONE" -> {
                            val updatedTask = task.copy(isCompleted = true)
                            dao.updateTask(updatedTask)
                            ReminderScheduler.cancel(context, taskId)
                            SoundManager.playSuccess()
                        }
                        "ACTION_SNOOZE" -> {
                            val snoozeTime = System.currentTimeMillis() + 10 * 60 * 1000L // 10 minutes
                            val updatedTask = task.copy(reminderTime = snoozeTime)
                            dao.updateTask(updatedTask)
                            ReminderScheduler.schedule(context, updatedTask)
                            SoundManager.playTap()
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
