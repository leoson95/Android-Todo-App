package com.example.util

import com.example.BuildConfig
import com.example.data.Category
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AiTaskResult(
    val title: String,
    val description: String = "",
    val categoryName: String? = null,
    val reminderTime: Long? = null,
    val subtasks: List<String> = emptyList()
)

object AiManager {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, AiTaskResult::class.java)
    private val adapter = moshi.adapter<List<AiTaskResult>>(listType)

    private fun getModel(apiKey: String): GenerativeModel {
        val finalKey = if (apiKey.isNotBlank()) apiKey else BuildConfig.GEMINI_API_KEY
        return GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = finalKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            }
        )
    }

    suspend fun processTasks(
        input: String,
        apiKey: String,
        existingCategories: List<Category>,
        currentJalaliDate: JalaliCalendar.JalaliDate,
        currentGregorianDate: String
    ): List<AiTaskResult> = withContext(Dispatchers.IO) {
        val categoryList = existingCategories.joinToString { it.name }
        
        val systemPrompt = """
            You are a smart task assistant. Extract tasks from the user's input.
            Current Jalali Date: ${currentJalaliDate.year}/${currentJalaliDate.month}/${currentJalaliDate.day}
            Current Gregorian Date: $currentGregorianDate
            Available Categories: $categoryList
            
            Rules:
            1. Extract title and description.
            2. Extract reminderTime as a timestamp in milliseconds.
            3. Match to an existing category if possible, else suggest a new one.
            4. Detect subtasks if mentioned.
            5. If no specific year is mentioned for dates like 'tomorrow' or 'next week', use the current year.
            6. Return a JSON list of objects with fields: title (string), description (string), categoryName (string), reminderTime (long or null), subtasks (list of strings).
            
            Example Input: "Tomorrow 8 AM buy bread"
            Output: [{"title": "Buy bread", "description": "", "categoryName": "Personal", "reminderTime": 1700000000000, "subtasks": []}]
        """.trimIndent()

        try {
            val response = getModel(apiKey).generateContent(
                content {
                    text(systemPrompt)
                    text("User Input: $input")
                }
            )
            val json = response.text ?: return@withContext emptyList()
            return@withContext adapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}
