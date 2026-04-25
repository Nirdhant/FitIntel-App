package com.example.pdf

import android.graphics.Bitmap
import android.util.Log
import com.example.data.model.HealthData
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TextExtractor {

    private val TAG = "TextExtractor"
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private fun parseHealthData(text: String): HealthData {
        Log.d(TAG, "========== PARSING STARTED ==========")

        val upperText = text.uppercase()
        val lines = text.lines()

        var age: Int? = null
        val ageMatch = Regex("""(\d{2})\s*/\s*FEMALE""", RegexOption.IGNORE_CASE).find(upperText)
        if (ageMatch != null) {
            age = ageMatch.groupValues[1].toIntOrNull()
            Log.d(TAG, "✓ Age: $age")
        }

        var hemoglobin: Float? = null
        for (line in lines) {
            val match = Regex("""(\d{1,2}\.\d{1})""").find(line)
            if (match != null) {
                val value = match.value.toFloatOrNull()
                if (value != null && value in 1.0..20.0) {
                    hemoglobin = value
                    Log.d(TAG, "✓ Hemoglobin: $hemoglobin")
                    break
                }
            }
        }

        var cholesterol: Float? = null

        val serumCholIndex = lines.indexOfFirst {
            it.contains("SERUM CHOLESTEROL", ignoreCase = true) ||
                    it.contains("TOTAL CHOLESTEROL", ignoreCase = true)
        }
        if (serumCholIndex != -1) {
            Log.d(TAG, "Found SERUM CHOLESTEROL at line $serumCholIndex")

            for (i in serumCholIndex + 1 until minOf(serumCholIndex + 11, lines.size)) {
                val lineText = lines[i].trim()

                val cholMatch = Regex("""^(\d{2,3})(?:\.\d+)?$""").find(lineText)
                if (cholMatch != null) {
                    val value = cholMatch.groupValues[1].toFloatOrNull()
                    if (value != null && value in 100.0..500.0) {  // Serum cholesterol range
                        cholesterol = value
                        Log.d(TAG, "✓ SERUM Cholesterol: $cholesterol (from line $i: '$lineText')")
                        break
                    }
                }
            }
        }

        if (cholesterol == null) {
            val ldlCholIndex = lines.indexOfFirst {
                it.contains("LDL CHOLESTEROL", ignoreCase = true)
            }

            if (ldlCholIndex != -1) {
                Log.d(TAG, "Found LDL CHOLESTEROL at line $ldlCholIndex")

                for (i in ldlCholIndex + 1 until minOf(ldlCholIndex + 11, lines.size)) {
                    val lineText = lines[i].trim()

                    val cholMatch = Regex("""^(\d{2,3})(?:\.\d+)?$""").find(lineText)
                    if (cholMatch != null) {
                        val value = cholMatch.groupValues[1].toFloatOrNull()
                        if (value != null && value in 50.0..300.0) {  // LDL range
                            cholesterol = value
                            Log.d(TAG, "✓ LDL Cholesterol: $cholesterol (from line $i: '$lineText')")
                            break
                        }
                    }
                }
            }
        }

        if (cholesterol == null) {
            val genericCholIndex = lines.indexOfFirst {
                it.contains("CHOLESTEROL", ignoreCase = true)
            }

            if (genericCholIndex != -1) {
                Log.d(TAG, "Found generic CHOLESTEROL at line $genericCholIndex")

                for (i in genericCholIndex + 1 until minOf(genericCholIndex + 11, lines.size)) {
                    val lineText = lines[i].trim()

                    val cholMatch = Regex("""^(\d{2,3})(?:\.\d+)?$""").find(lineText)
                    if (cholMatch != null) {
                        val value = cholMatch.groupValues[1].toFloatOrNull()
                        if (value != null && value in 50.0..500.0) {
                            cholesterol = value
                            Log.d(TAG, "✓ Generic Cholesterol: $cholesterol (from line $i: '$lineText')")
                            break
                        }
                    }
                }
            }
        }

        var sugar: Float? = null
        val sugarPatterns = listOf(
            Regex("""GLUCOSE.*?(\d{2,3})""", RegexOption.IGNORE_CASE),
            Regex("""SUGAR.*?(\d{2,3})""", RegexOption.IGNORE_CASE)
        )

        for (pattern in sugarPatterns) {
            val match = pattern.find(upperText)
            if (match != null) {
                sugar = match.groupValues[1].toFloatOrNull()
                if (sugar != null && sugar in 50.0..400.0) {
                    Log.d(TAG, "✓ Sugar: $sugar")
                    break
                }
            }
        }

        val healthData = HealthData(
            age = age,
            bloodGroup = null,
            bmi = null,
            sugarLevel = sugar,
            hemoglobinLevel = hemoglobin,
            cholesterolLevel = cholesterol,
            heartRate = null,
            caloriesBurned = null
        )

        Log.d(TAG, "========== EXTRACTION COMPLETE ==========")
        Log.d(TAG, "Final: Age=$age, Hb=$hemoglobin, Chol=$cholesterol, Sugar=$sugar")
        Log.d(TAG, "=========================================")

        return healthData
    }

    suspend fun extractTextFromBitmap(bitmaps: List<Bitmap>): Result<HealthData> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting OCR on ${bitmaps.size} pages")

            val allText = StringBuilder()

            bitmaps.forEachIndexed { index, bitmap ->
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                val result = textRecognizer.process(inputImage).await()
                Log.d(TAG, "Page ${index + 1} extracted ${result.text.length} chars")
                allText.append(result.text).append("\n")
            }

            val healthData = parseHealthData(allText.toString())

            Result.success(healthData)

        } catch (e: Exception) {
            Log.e(TAG, "OCR failed", e)
            Result.failure(Exception("Text extraction failed: ${e.message}", e))
        }
    }

    fun cleanUp() {
        textRecognizer.close()
    }
}