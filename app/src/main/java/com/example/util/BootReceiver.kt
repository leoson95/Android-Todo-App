package com.example.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val pendingResult = goAsync()
            val dao = AppDatabase.getDatabase(context).todoDao()
            
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tasks = dao.getAllTasksList()
                    val currentTime = System.currentTimeMillis()
                    for (task in tasks) {
                        if (!task.isCompleted && task.reminderTime != null && task.reminderTime > currentTime) {
                            ReminderScheduler.schedule(context, task)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
