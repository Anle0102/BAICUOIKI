package com.example.baicuoiki.util

import com.example.baicuoiki.data.CardExport
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiHelper(apiKey: String) {
    // Sửa modelName thành "gemini-1.5-flash-latest" hoặc "gemini-1.5-flash" 
    // và kiểm tra lại cấu hình GenerativeModel cho bản 0.9.0
    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash", 
        apiKey = apiKey
    )

    suspend fun generateFlashcards(topic: String): List<CardExport> = withContext(Dispatchers.IO) {
        val prompt = "Tạo 5 thẻ ghi nhớ về chủ đề: $topic dưới dạng JSON Tiếng Việt. Định dạng: [{\"front\": \"...\", \"back\": \"...\"}]"
        try {
            val response = model.generateContent(prompt)
            val jsonText = response.text?.trim() ?: ""
            val cleanedJson = if (jsonText.startsWith("```")) {
                jsonText.substringAfter("json").substringBeforeLast("```").trim()
            } else jsonText
            val type = object : TypeToken<List<CardExport>>() {}.type
            return@withContext Gson().fromJson(cleanedJson, type)
        } catch (e: Exception) {
            return@withContext emptyList()
        }
    }

    suspend fun chat(message: String): String = withContext(Dispatchers.IO) {
        try {
            // Sử dụng content block để thiết lập system instruction hoặc nhắc nhở
            val response = model.generateContent(content {
                text("Bạn là trợ lý học tập. Hãy luôn trả lời bằng Tiếng Việt.")
                text(message)
            })
            return@withContext response.text ?: "AI không phản hồi."
        } catch (e: Exception) {
            // Trả về lỗi chi tiết hơn để dễ debug
            return@withContext "Lỗi AI: ${e.localizedMessage}"
        }
    }
}
