package com.example.gemini

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthData
import com.example.data.state.AppState
import kotlinx.coroutines.launch

@Composable
fun GeminiScreen() {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(AppState.healthData) {
        if (AppState.healthData != null && AppState.geminiResponse == null) {
            AppState.startProcessing("Generating AI response...")

            coroutineScope.launch {
                val prompt = buildHealthPrompt(AppState.healthData!!)
                val response = FirebaseGeminiLogic.sendPrompt(prompt)

                AppState.setGeminiResponse(response)
                AppState.stopProcessing()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (AppState.geminiResponse != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            ) {
                item {
                    ResponseUi(AppState.geminiResponse!!)
                }
            }
        } else if (AppState.healthData == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier=Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.fitintel),
                        contentDescription = null,
                        contentScale = ContentScale.Fit
                    )
                    Text(
                        text = "No health report uploaded",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Please upload a PDF to see AI analysis",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.5f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Bottom Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp)
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier.size(30.dp),
                    painter = painterResource(id = R.drawable.gemini),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(3.dp))

                Image(
                    modifier = Modifier.size(90.dp),
                    painter = painterResource(id = R.drawable.gemini_text),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(Color.Black)
                )
            }
        }
    }
}
private fun buildHealthPrompt(healthData: HealthData): String {
    return buildString {
        append(
            "You are a medical assistant. Analyze the following health data and " +
                    "create a clear, patient‑friendly report in simple English.\n\n"
        )

        append("## Patient Data\n")
        healthData.age?.let { append("- Age: $it years\n") }
        healthData.bloodGroup?.let { append("- Blood Group: $it\n") }
        healthData.bmi?.let { append("- BMI: $it\n") }
        healthData.sugarLevel?.let { append("- Sugar Level: $it mg/dL\n") }
        healthData.hemoglobinLevel?.let { append("- Hemoglobin: $it g/dL\n") }
        healthData.cholesterolLevel?.let { append("- Cholesterol: $it mg/dL\n") }
        healthData.heartRate?.let { append("- Heart Rate: $it bpm\n") }
        healthData.caloriesBurned?.let { append("- Calories Burned: $it\n") }

        append("\n## Output format (very important)\n")
        append(
            "- Respond in **Markdown**.\n" +
                    "- Use the following sections in this exact order.\n" +
                    "- Each section title must be in **bold**.\n" +
                    "- Under every section, use a numbered list (1., 2., 3., ...).\n" +
                    "- Each point should be short (1–2 sentences) but complete and specific.\n\n"
        )

        append("1. **Health Status Assessment**\n")
        append(
            "   1. Summarize the overall health status based on the given values.\n" +
                    "   2. Mention any values that are clearly normal, borderline, or concerning.\n" +
                    "   3. Keep the language simple so a non‑medical person can understand.\n\n"
        )

        append("2. **Vegetarian Diet Plan**\n")
        append(
            "   1. Give a short daily or weekly vegetarian diet plan.\n" +
                    "   2. Focus on foods that support the given health data (for example, weight, sugar, cholesterol).\n" +
                    "   3. Include 3–5 specific food or meal suggestions.\n\n"
        )

        append("3. **Non‑Vegetarian Diet Plan**\n")
        append(
            "   1. Give a short daily or weekly non‑vegetarian diet plan.\n" +
                    "   2. Focus on lean protein and heart‑healthy choices.\n" +
                    "   3. Include 3–5 specific food or meal suggestions.\n\n"
        )

        append("4. **Exercise Recommendations**\n")
        append(
            "   1. Suggest simple exercises with approximate duration and frequency (for example, 30 minutes, 5 days a week).\n" +
                    "   2. Cover both cardio and strength or flexibility.\n" +
                    "   3. Adapt intensity if values like BMI, sugar, or heart rate are abnormal.\n\n"
        )

        append("5. **Health Warnings (if any)**\n")
        append(
            "   1. Clearly mention any serious or urgent warning signs based only on the given data.\n" +
                    "   2. Advise when the user should consult a doctor or specialist.\n" +
                    "   3. Keep this section short and direct.\n\n"
        )

        append("6. **Summary & Next Steps**\n")
        append(
            "   1. Give a short summary of the main issues and strengths.\n" +
                    "   2. List 2–3 most important actions the user should start immediately.\n" +
                    "   3. Keep the tone encouraging but realistic.\n"
        )
    }
}
