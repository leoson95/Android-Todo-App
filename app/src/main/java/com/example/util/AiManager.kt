package com.example.util

import com.example.BuildConfig
import com.example.data.Category
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
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

    suspend fun fetchAvailableModels(apiKey: String): List<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) return@withContext emptyList()
        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models?key=$apiKey"
            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext emptyList()
                val body = response.body?.string() ?: return@withContext emptyList()
                
                // Parse models from the response
                val jsonAdapter = moshi.adapter(Map::class.java)
                val jsonMap = jsonAdapter.fromJson(body)
                val modelsList = jsonMap?.get("models") as? List<Map<String, Any>>
                
                return@withContext modelsList?.mapNotNull { modelMap ->
                    val name = modelMap["name"] as? String
                    val methods = modelMap["supportedGenerationMethods"] as? List<*>
                    if (name?.startsWith("models/gemini") == true && methods?.contains("generateContent") == true) {
                        name.removePrefix("models/")
                    } else null
                } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun getModel(apiKey: String, modelName: String): GenerativeModel {
        val finalKey = if (apiKey.isNotBlank()) apiKey else BuildConfig.GEMINI_API_KEY
        val finalModelName = if (modelName.isNotBlank()) modelName else "gemini-1.5-flash"
        return GenerativeModel(
            modelName = finalModelName,
            apiKey = finalKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
            safetySettings = listOf(
                SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, BlockThreshold.ONLY_HIGH),
                SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.ONLY_HIGH)
            )
        )
    }

    suspend fun processTasks(
        input: String,
        apiKey: String,
        modelName: String,
        existingCategories: List<Category>,
        currentJalaliDate: JalaliCalendar.JalaliDate,
        currentGregorianDate: String
    ): List<AiTaskResult> = withContext(Dispatchers.IO) {
        val categoryList = existingCategories.joinToString { it.name }
        
        val systemPrompt = """
            You are a task management assistant.
            Convert user input into a JSON list of task objects.
            Current Jalali: ${currentJalaliDate.year}/${currentJalaliDate.month}/${currentJalaliDate.day}
            Current Gregorian: $currentGregorianDate
            Existing Categories: $categoryList
            
            JSON Schema:
            - title: string (required)
            - description: string (optional)
            - categoryName: string (match existing or suggest new)
            - reminderTime: long (milliseconds timestamp or null)
            - subtasks: list of strings
            
            IMPORTANT: Return ONLY raw JSON. No markdown. No text outside the JSON list.
        """.trimIndent()

        try {
            val response = getModel(apiKey, modelName).generateContent(
                content {
                    text(systemPrompt)
                    text("Input: $input")
                }
            )
            
            val rawText = response.text ?: throw Exception("AI returned empty text.")
            
            var cleanJson = rawText.trim()
            if (cleanJson.contains("[")) {
                val start = cleanJson.indexOf("[")
                val end = cleanJson.lastIndexOf("]")
                if (start != -1 && end != -1 && end > start) {
                    cleanJson = cleanJson.substring(start, end + 1)
                }
            }
            
            return@withContext adapter.fromJson(cleanJson) 
                ?: throw Exception("Failed to parse AI response.")
                
        } catch (e: Exception) {
            throw Exception("Gemini Error: ${e.localizedMessage ?: "Unknown"}")
        }
    }
}
