package com.example.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProgressIndicator(
    label: String,
    innerColor: Color,
    outerColor: Color,
    percentage: Float = 0f,
    steps: Boolean = false,
    stepsCount: Int = 0
){
    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center){
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.fillMaxSize(),
            color = innerColor.copy(alpha = 0.9f),
            strokeWidth = 14.dp ,
            strokeCap = StrokeCap.Round,
        )
        CircularProgressIndicator(
            progress = {
                if (!steps){
                    percentage
                }
                else {
                   stepsCount.div(100f).toFloat()
                }

                       },
            modifier = Modifier.fillMaxSize(),
            color = outerColor,
            trackColor = Color.Transparent,
            strokeWidth = 14.dp,
            strokeCap = StrokeCap.Square,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text =
                if (!steps){
                    "${(percentage * 100).toInt()}%"
                }
                else {"$stepsCount"} ,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}