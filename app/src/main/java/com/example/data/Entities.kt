package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "categories",
    indices = [Index(value = ["name"], unique = true)]
)
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String,
    val isDefault: Boolean = false,
    @ColumnInfo(defaultValue = "0") val orderIndex: Int = 0
)

@Entity(
    tableName = "tasks",
    indices = [Index(value = ["categoryId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val categoryId: Int,
    val isCompleted: Boolean = false,
    val reminderTime: Long? = null, // timestamp in millis
    val repeatType: String? = null, // "none", "daily", "every_other_day", "weekly"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subtasks",
    indices = [Index(value = ["taskId"])]
)
data class Subtask(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val taskId: Int,
    val title: String,
    val isCompleted: Boolean = false
)
