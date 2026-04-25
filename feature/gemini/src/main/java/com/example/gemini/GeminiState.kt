package com.example.gemini

sealed class GeminiState {

    object Initial : GeminiState()
    object Loading : GeminiState()
    data class Success(val response: String) : GeminiState()
    data class Error(val message: String) : GeminiState()
}