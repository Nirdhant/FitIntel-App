package com.example.pdf

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.HealthData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.data.state.AppState

class PdfViewModel : ViewModel() {

    private val _pdfState = MutableStateFlow<PdfState>(PdfState.Initial)
    val pdfState: StateFlow<PdfState> = _pdfState.asStateFlow()

    private val textExtractor = TextExtractor()

    fun processPdf(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                _pdfState.value = PdfState.ConvertingPdf
                val bitmapResult = PdfToBitmapConverter.convert(context, uri)

                if (bitmapResult.isFailure) {
                    _pdfState.value = PdfState.Error(
                        bitmapResult.exceptionOrNull()?.message
                            ?: "Failed to convert PDF"
                    )
                    return@launch
                }

                val bitmaps = bitmapResult.getOrNull() ?: return@launch

                _pdfState.value = PdfState.ExtractingText
                val healthDataResult = textExtractor.extractTextFromBitmap(bitmaps)

                if (healthDataResult.isFailure) {
                    _pdfState.value = PdfState.Error(
                        healthDataResult.exceptionOrNull()?.message
                            ?: "Failed to extract text from PDF"
                    )
                    return@launch
                }
                val healthData = healthDataResult.getOrNull()
                if (healthData != null && isValidHealthData(healthData)) {
                    // 1) Save globally for dashboard
                    AppState.setHealthData(healthData)

                    // 2) Notify UI that parsing succeeded
                    _pdfState.value = PdfState.Success(healthData)
                } else {
                    _pdfState.value = PdfState.Error(
                        "No valid health data found in PDF.\n\n" +
                                "Please upload a proper health report containing at least 2 of these:\n" +
                                "• Age\n" +
                                "• Blood Group\n" +
                                "• BMI\n" +
                                "• Sugar/Glucose Level\n" +
                                "• Hemoglobin Level\n" +
                                "• Cholesterol Level"
                    )
                }

            } catch (e: Exception) {
                _pdfState.value = PdfState.Error(
                    "Error processing PDF: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
    private fun isValidHealthData(healthData: HealthData): Boolean {
        var validFieldCount = 0
        if (healthData.age != null) validFieldCount++
        if (healthData.bloodGroup != null) validFieldCount++
        if (healthData.bmi != null) validFieldCount++
        if (healthData.sugarLevel != null) validFieldCount++
        if (healthData.hemoglobinLevel != null) validFieldCount++
        if (healthData.cholesterolLevel != null) validFieldCount++
        if (healthData.heartRate != null) validFieldCount++
        if (healthData.caloriesBurned != null) validFieldCount++

        return validFieldCount >= 2
    }

    fun resetState() {
        _pdfState.value = PdfState.Initial
    }

    override fun onCleared() {
        super.onCleared()
        textExtractor.cleanUp()
    }
}