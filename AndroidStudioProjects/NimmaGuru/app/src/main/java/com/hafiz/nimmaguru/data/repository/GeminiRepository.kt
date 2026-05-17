package com.hafiz.nimmaguru.data.repository

import com.google.ai.client.generativeai.GenerativeModel
import com.hafiz.nimmaguru.BuildConfig

class GeminiRepository {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    suspend fun generateBio(name: String, skills: List<String>, village: String): Result<String> {
        return try {
            val skillsText = skills.joinToString(", ")
            val prompt = """
                Write a warm, professional 3-sentence bio for a community mentor named $name 
                who voluntarily teaches $skillsText to students in $village, Karnataka, India.
                This person is a retired professional doing this as Gyaan-Daan (knowledge donation).
                Keep it simple, inspiring, and in English. Do not use bullet points.
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: "Could not generate bio. Please write one manually."
            Result.success(text.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateSessionTip(subject: String, level: String = "school students"): Result<String> {
        return try {
            val prompt = """
                Give 2 quick teaching tips for a volunteer mentor teaching $subject to $level 
                in a community center in Karnataka, India. Keep it practical and short (max 3 sentences).
            """.trimIndent()

            val response = model.generateContent(prompt)
            val text = response.text ?: "Prepare simple examples from daily life. Encourage questions."
            Result.success(text.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
