package com.example.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.data.state.AppState
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    stepsCount: Int = 0,
    onLogoutClick: () -> Unit = {}
) {
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "Unknown user"
    val healthData = AppState.healthData

    // Default scores (0–100) if data is missing
    val caloriesScore = (healthData?.caloriesBurned ?: 10).coerceIn(0, 100)
    val sugarScore = (healthData?.sugarLevel?.toInt() ?: 10).coerceIn(0, 100)
    val hbScore = when (val hb = healthData?.hemoglobinLevel) {
        null -> 16
        in 12f..15f -> 85
        in 10f..11.9f -> 65
        in 8f..9.9f -> 40
        else -> 25
    }
    val cholScore = when (val c = healthData?.cholesterolLevel) {
        null -> 16
        in 0f..150f -> 50
        in 150f..200f -> 70
        in 200f..240f -> 85
        else -> 95
    }
    val heartRateScore = (healthData?.heartRate ?: 10).coerceIn(0, 100)

    // Convert to 0f..1f as ProgressIndicator expects
    val caloriesPercent = caloriesScore / 100f
    val sugarPercent = sugarScore / 100f
    val hbPercent = hbScore / 100f
    val cholesterolPercent = cholScore / 100f
    val heartRatePercent = heartRateScore / 100f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp) // overall screen padding
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Health snapshot card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Health Snapshot",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Top row: Calories, Sugar, Haemoglobin
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProgressIndicator(
                        label = "Calories",
                        // Bright orange theme
                        innerColor = Color(0xFFFF6F00),        // deep orange
                        outerColor = Color(0xFFFFE082),        // light amber
                        percentage = caloriesPercent
                    )
                    ProgressIndicator(
                        label = "Sugar",
                        // Red / pink theme
                        innerColor = Color(0xFFD32F2F),        // strong red
                        outerColor = Color(0xFFFFCDD2),        // light pink
                        percentage = sugarPercent
                    )
                    ProgressIndicator(
                        label = "Haemoglobin",
                        // Purple theme
                        innerColor = Color(0xFF7B1FA2),        // deep purple
                        outerColor = Color(0xFFE1BEE7),        // light lavender
                        percentage = hbPercent
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom row: Cholesterol, Heart Rate
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProgressIndicator(
                        label = "Cholesterol",
                        // Blue theme
                        innerColor = Color(0xFF1976D2),        // deep blue
                        outerColor = Color(0xFFBBDEFB),        // light blue
                        percentage = cholesterolPercent
                    )
                    ProgressIndicator(
                        label = "Heart Rate",
                        // Orange/red theme
                        innerColor = Color(0xFFE64A19),        // burnt orange
                        outerColor = Color(0xFFFFCCBC),        // light orange
                        percentage = heartRatePercent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Activity & location card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Activity & Location",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProgressIndicator(
                        label = "Steps Count",
                        // Green theme for steps
                        innerColor = Color(0xFF2E7D32),        // deep green
                        outerColor = Color(0xFFC8E6C9),        // light green
                        steps = true,
                        stepsCount = stepsCount
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Address",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Open the Maps tab to view your live location and route.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Account card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Account",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Logged in as:",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = onLogoutClick) {
                    Text("Log out")
                }
            }
        }
    }
}
