package com.example.data.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.data.model.HealthData

object AppState {
    private val _healthData: MutableState<HealthData?> = mutableStateOf(null)
    private val _geminiResponse: MutableState<String?> = mutableStateOf(null)
    private val _isProcessing: MutableState<Boolean> = mutableStateOf(false)
    private val _processingMessage: MutableState<String> = mutableStateOf("")

    val healthData: HealthData?
        get() = _healthData.value

    val geminiResponse: String?
        get() = _geminiResponse.value

    val isProcessing: Boolean
        get() = _isProcessing.value

    val processingMessage: String
        get() = _processingMessage.value


    fun setHealthData(data: HealthData) {
        _healthData.value = data
    }

    fun setGeminiResponse(response: String) {
        _geminiResponse.value = response
    }

    fun startProcessing(message: String) {
        _isProcessing.value = true
        _processingMessage.value = message
    }

    fun stopProcessing() {
        _isProcessing.value = false
        _processingMessage.value = ""
    }


    fun reset() {
        _healthData.value = null
        _geminiResponse.value = null
        _isProcessing.value = false
        _processingMessage.value = ""
    }
}