package com.example.ui

import android.graphics.Bitmap
import android.util.Base64
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

object GeminiFoodHelper {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun Bitmap.toBase64(): String {
        val outputStream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    suspend fun analyzeFoodImage(bitmap: Bitmap): ResultState = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext ResultState.Error("Ключ API не настроен. Пожалуйста, добавьте GEMINI_API_KEY в панели secrets панели AI Studio.")
        }

        val base64Image = bitmap.toBase64()
        val systemPrompt = "Ты — умный анализатор еды по фотографии для приложения 'Счетчик калорий'. Тщательно распознай блюдо на изображении и укажи его примерную КБЖУ (калории, белки, жиры, углеводы) ценность в одной порции. Все названия и тексты должны быть строго на русском языке. Ответ должен быть строго в формате JSON, без какого-либо окружающего текста или разметки markdown. Формат ответа: { \\\"dish_name\\\": \\\"название блюда\\\", \\\"calories\\\": число_калорий, \\\"proteins\\\": число_белков, \\\"fats\\\": число_жиров, \\\"carbs\\\": число_углеводов }"

        val jsonRequest = """
            {
              "contents": [
                {
                  "parts": [
                    { "text": "$systemPrompt" },
                    { "inlineData": { "mimeType": "image/jpeg", "data": "$base64Image" } }
                  ]
                }
              ],
              "generationConfig": {
                "responseMimeType": "application/json",
                "temperature": 0.2
              }
            }
        """.trimIndent()

        val requestBody = jsonRequest.toRequestBody("application/json".toMediaType())
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext ResultState.Error("Ошибка API: ${response.code} ${response.message}")
            }
            val responseBody = response.body?.string() ?: return@withContext ResultState.Error("Пустой ответ от сервера")
            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext ResultState.Error("Не удалось распознать блюдо. Попробуйте еще раз.")
            }
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return@withContext ResultState.Error("Отсутствует контент")
            val parts = content.optJSONArray("parts") ?: return@withContext ResultState.Error("Отсутствуют части контента")
            if (parts.length() == 0) {
                return@withContext ResultState.Error("Отсутствует текст в ответе")
            }
            val text = parts.getJSONObject(0).optString("text")
            
            // Parse the JSON returned by Gemini
            val parsedJson = JSONObject(text.trim())
            val dishName = parsedJson.optString("dish_name", "Неизвестное блюдо")
            val calories = parsedJson.optInt("calories", 0)
            val proteins = parsedJson.optInt("proteins", 0)
            val fats = parsedJson.optInt("fats", 0)
            val carbs = parsedJson.optInt("carbs", 0)

            ResultState.Success(dishName, calories, proteins, fats, carbs)
        } catch (e: Exception) {
            ResultState.Error("Не удалось разобрать ответ: ${e.localizedMessage ?: e.message}")
        }
    }

    sealed class ResultState {
        object Idle : ResultState()
        object Loading : ResultState()
        data class Success(
            val dish: String,
            val calories: Int,
            val proteins: Int = 0,
            val fats: Int = 0,
            val carbs: Int = 0
        ) : ResultState()
        data class Error(val message: String) : ResultState()
    }
}
