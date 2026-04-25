package com.example.gemini

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend

object FirebaseGeminiLogic {

    private var model: GenerativeModel? = null
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            try {
                FirebaseApp.initializeApp(context)
                model = Firebase
                    .ai(backend = GenerativeBackend.googleAI())
                    .generativeModel("gemini-2.5-flash-lite")
                isInitialized = true
            } catch (e: Exception) {
                throw Exception("Failed to initialize Gemini AI: ${e.message}")
            }
        }
    }
    suspend fun sendPrompt(prompt: String): String {
        // Check if initialized
        if (!isInitialized || model == null) {
            return "Error: Gemini AI not initialized. Call init() first."
        }

        return try {
            val response = model?.generateContent(prompt)
            response?.text?.let {
                if (it.isNotBlank()) it else "No response generated"
            } ?: "No response from AI"

        } catch (e: Exception) {
            "Error: ${e.message ?: "Unknown error occurred"}"
        }
    }
}