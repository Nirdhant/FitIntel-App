package com.example.pdf

import com.example.data.model.HealthData


sealed class PdfState {
    object Initial : PdfState()
    object ConvertingPdf : PdfState()
    object ExtractingText : PdfState()
    data class Success(val healthData: HealthData) : PdfState()
    data class Error(val message: String) : PdfState()
}