package com.example.ui

import android.app.Application
import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AppDatabase
import com.example.data.Category
import com.example.data.Subtask
import com.example.data.Task
import com.example.data.TodoRepository
import com.example.util.AiManager
import com.example.util.AiTaskResult
import com.example.util.JalaliCalendar
import com.example.util.SoundManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BackupData(
    val categories: List<Category>,
    val tasks: List<Task>,
    val subtasks: List<Subtask>
)

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository
    private val sharedPrefs = application.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isFirstRun = MutableStateFlow(sharedPrefs.getBoolean("is_first_run", true))
    val isFirstRun: StateFlow<Boolean> = _isFirstRun.asStateFlow()

    private val _appLanguage = MutableStateFlow("system")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _geminiApiKey = MutableStateFlow(sharedPrefs.getString("gemini_api_key", "") ?: "")
    val geminiApiKey: StateFlow<String> = _geminiApiKey.asStateFlow()

    private val _selectedAiModel = MutableStateFlow(sharedPrefs.getString("selected_ai_model", "gemini-1.5-flash") ?: "gemini-1.5-flash")
    val selectedAiModel: StateFlow<String> = _selectedAiModel.asStateFlow()

    private val _availableAiModels = MutableStateFlow<List<String>>(emptyList())
    val availableAiModels: StateFlow<List<String>> = _availableAiModels.asStateFlow()

    private val _aiProcessedTasks = MutableStateFlow<List<AiTaskResult>>(emptyList())
    val aiProcessedTasks: StateFlow<List<AiTaskResult>> = _aiProcessedTasks.asStateFlow()

    private val _isAiProcessing = MutableStateFlow(false)
    val isAiProcessing: StateFlow<Boolean> = _isAiProcessing.asStateFlow()

    private val _aiErrorMessage = MutableStateFlow<String?>(null)
    val aiErrorMessage: StateFlow<String?> = _aiErrorMessage.asStateFlow()

    val categories: StateFlow<List<Category>>
    val tasks: StateFlow<List<Task>>
    val subtasks: StateFlow<List<Subtask>>

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TodoRepository(database.todoDao(), application)

        categories = repository.categories.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        tasks = repository.tasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        subtasks = repository.subtasks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        _isDarkTheme.value = sharedPrefs.getBoolean("dark_theme", false)
        _appLanguage.value = sharedPrefs.getString("app_language", "system") ?: "system"
        viewModelScope.launch {
            repository.seedDefaultCategoriesIfEmpty()
        }
    }

    fun toggleTheme() {
        SoundManager.playTap()
        val newValue = !_isDarkTheme.value
        _isDarkTheme.value = newValue
        sharedPrefs.edit().putBoolean("dark_theme", newValue).apply()
    }

    fun setLanguage(language: String) {
        SoundManager.playTap()
        _appLanguage.value = language
        sharedPrefs.edit().putString("app_language", language).apply()
    }

    fun setGeminiApiKey(key: String) {
        _geminiApiKey.value = key
        sharedPrefs.edit().putString("gemini_api_key", key).apply()
    }

    fun setAiModel(model: String) {
        _selectedAiModel.value = model
        sharedPrefs.edit().putString("selected_ai_model", model).apply()
    }

    fun loadAvailableModels() {
        viewModelScope.launch {
            val key = _geminiApiKey.value
            if (key.isBlank()) {
                Toast.makeText(getApplication(), "Enter API Key first!", Toast.LENGTH_SHORT).show()
                return@launch
            }
            _isAiProcessing.value = true
            val models = AiManager.fetchAvailableModels(key)
            if (models.isNotEmpty()) {
                _availableAiModels.value = models
            } else {
                Toast.makeText(getApplication(), "Could not load models.", Toast.LENGTH_SHORT).show()
            }
            _isAiProcessing.value = false
        }
    }

    fun markFirstRunDone() {
        _isFirstRun.value = false
        sharedPrefs.edit().putBoolean("is_first_run", false).apply()
    }

    fun addTask(
        title: String,
        description: String,
        categoryId: Int,
        reminderTime: Long?,
        repeatType: String?,
        subtasksList: List<String>
    ) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                categoryId = categoryId,
                reminderTime = reminderTime,
                repeatType = repeatType
            )
            repository.insertTask(task, subtasksList)
        }
    }

    fun editTask(
        taskId: Int,
        title: String,
        description: String,
        categoryId: Int,
        reminderTime: Long?,
        repeatType: String?,
        subtasksList: List<Subtask>
    ) {
        viewModelScope.launch {
            val existingTask = tasks.value.find { it.id == taskId } ?: return@launch
            val updatedTask = existingTask.copy(
                title = title,
                description = description,
                categoryId = categoryId,
                reminderTime = reminderTime,
                repeatType = repeatType
            )
            repository.updateTask(updatedTask)

            val oldSubtasks = subtasks.value.filter { it.taskId == taskId }
            val newIds = subtasksList.map { it.id }.filter { it != 0 }.toSet()

            for (old in oldSubtasks) {
                if (old.id !in newIds) {
                    repository.deleteSubtask(old)
                }
            }

            for (newSub in subtasksList) {
                if (newSub.id == 0) {
                    repository.insertSubtask(newSub)
                } else {
                    val old = oldSubtasks.find { it.id == newSub.id }
                    if (old != null && old.title != newSub.title) {
                        repository.updateSubtask(newSub)
                    }
                }
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            repository.toggleTaskCompleted(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addSubtask(taskId: Int, title: String) {
        viewModelScope.launch {
            val sub = Subtask(taskId = taskId, title = title)
            repository.insertSubtask(sub)
        }
    }

    fun toggleSubtaskCompleted(subtask: Subtask) {
        viewModelScope.launch {
            repository.toggleSubtaskCompleted(subtask)
        }
    }

    fun deleteSubtask(subtask: Subtask) {
        viewModelScope.launch {
            repository.deleteSubtask(subtask)
        }
    }

    fun addCategory(name: String, colorHex: String) {
        viewModelScope.launch {
            val cat = Category(name = name, colorHex = colorHex)
            repository.insertCategory(cat)
            SoundManager.playTap()
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            repository.updateCategory(category)
            SoundManager.playTap()
        }
    }

    fun moveCategoryUp(category: Category) {
        viewModelScope.launch {
            val list = categories.value.filter { it.id != -1 }.toMutableList()
            val index = list.indexOfFirst { it.id == category.id }
            if (index > 0) {
                val temp = list[index - 1]
                list[index - 1] = list[index]
                list[index] = temp
                
                list.forEachIndexed { i, cat ->
                    if (cat.orderIndex != i) {
                        repository.updateCategory(cat.copy(orderIndex = i))
                    }
                }
                SoundManager.playTap()
            }
        }
    }

    fun moveCategoryDown(category: Category) {
        viewModelScope.launch {
            val list = categories.value.filter { it.id != -1 }.toMutableList()
            val index = list.indexOfFirst { it.id == category.id }
            if (index != -1 && index < list.size - 1) {
                val temp = list[index + 1]
                list[index + 1] = list[index]
                list[index] = temp
                
                list.forEachIndexed { i, cat ->
                    if (cat.orderIndex != i) {
                        repository.updateCategory(cat.copy(orderIndex = i))
                    }
                }
                SoundManager.playTap()
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
        }
    }

    fun processInputWithAi(input: String) {
        if (input.isBlank()) return
        
        val key = _geminiApiKey.value
        if (key.isBlank()) {
            _aiErrorMessage.value = "Please enter your Gemini API Key in Settings first!"
            return
        }

        viewModelScope.launch {
            _isAiProcessing.value = true
            _aiProcessedTasks.value = emptyList()
            _aiErrorMessage.value = null
            
            try {
                val jalaliDate = JalaliCalendar.getNowJalali()
                val gregorianDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                val results = AiManager.processTasks(
                    input = input,
                    apiKey = key,
                    modelName = _selectedAiModel.value,
                    existingCategories = categories.value,
                    currentJalaliDate = jalaliDate,
                    currentGregorianDate = gregorianDate
                )
                
                if (results.isEmpty()) {
                    _aiErrorMessage.value = "No tasks found in the text. Please describe them more clearly."
                }
                
                _aiProcessedTasks.value = results
            } catch (e: Exception) {
                val errorDetails = e.message ?: "Unknown Error"
                _aiErrorMessage.value = errorDetails
            } finally {
                _isAiProcessing.value = false
            }
        }
    }

    fun addAllAiTasks() {
        viewModelScope.launch {
            _aiProcessedTasks.value.forEach { aiTask ->
                var catId = categories.value.find { it.name.equals(aiTask.categoryName, ignoreCase = true) }?.id
                if (catId == null && !aiTask.categoryName.isNullOrBlank()) {
                    val newCat = Category(name = aiTask.categoryName, colorHex = "#3B82F6")
                    catId = repository.insertCategory(newCat).toInt()
                }
                
                addTask(
                    title = aiTask.title,
                    description = aiTask.description,
                    categoryId = catId ?: -1,
                    reminderTime = aiTask.reminderTime,
                    repeatType = "none",
                    subtasksList = aiTask.subtasks
                )
            }
            _aiProcessedTasks.value = emptyList()
        }
    }

    fun clearAiResults() {
        _aiProcessedTasks.value = emptyList()
        _aiErrorMessage.value = null
    }

    // --- Backup & Restore ---
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val backupAdapter = moshi.adapter(BackupData::class.java)

    fun exportBackup() {
        viewModelScope.launch {
            try {
                val data = BackupData(
                    categories = repository.getCategoriesList(),
                    tasks = repository.getAllTasksList(),
                    subtasks = repository.getAllSubtasksList()
                )
                val json = backupAdapter.toJson(data)
                
                withContext(Dispatchers.IO) {
                    val fileName = "MyTasks_Backup_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.json"
                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    val file = File(downloadsDir, fileName)
                    file.writeText(json)
                }
                
                Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.backup_success), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.backup_failed), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun importBackup(json: String) {
        viewModelScope.launch {
            try {
                val data = backupAdapter.fromJson(json) ?: throw Exception("Invalid data")
                
                data.categories.forEach { repository.insertCategory(it) }
                data.tasks.forEach { repository.insertTaskDirectly(it) }
                data.subtasks.forEach { repository.insertSubtask(it) }
                
                Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.restore_success), Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getApplication(), getApplication<Application>().getString(R.string.restore_failed), Toast.LENGTH_LONG).show()
            }
        }
    }
}
