package com.example.pdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.state.AppState
import com.example.ui.theme.textFieldContainer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PdfScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val textExtractor = remember { TextExtractor() }

    val getPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            AppState.startProcessing("Converting PDF...")
            Toast.makeText(context, "PDF Selected", Toast.LENGTH_SHORT).show()

            coroutineScope.launch {
                val result = PdfToBitmapConverter.convert(context, it)
                result.fold(
                    onSuccess = { bitmapList ->
                        AppState.startProcessing("Extracting text...")
                        Toast.makeText(context, "PDF Converted", Toast.LENGTH_SHORT).show()

                        val extractResult = textExtractor.extractTextFromBitmap(bitmapList)
                        extractResult.fold(
                            onSuccess = { healthData ->
                                AppState.startProcessing("Analyzing health data...")

                                AppState.setHealthData(healthData)

                                delay(500)

                                AppState.stopProcessing()

                                Toast.makeText(
                                    context,
                                    "✅ Analysis complete! Click 'Response' tab to view results.",
                                    Toast.LENGTH_LONG
                                ).show()

                            },
                            onFailure = { exception ->
                                AppState.stopProcessing()
                                Toast.makeText(
                                    context,
                                    "Extraction failed: ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    },
                    onFailure = { exception ->
                        AppState.stopProcessing()
                        Toast.makeText(
                            context,
                            "PDF Conversion failed: ${exception.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.9f)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    BorderStroke(2.dp, Color.Gray.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    modifier = Modifier
                        .size(400.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.pdf),
                    contentDescription = null
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            // ✅ Reset previous data before new upload
                            AppState.reset()
                            getPdfLauncher.launch("application/pdf")
                        },
                        colors = ButtonColors(
                            containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentColor = MaterialTheme.colorScheme.surfaceContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        enabled = !AppState.isProcessing,
                        modifier = Modifier.height(45.dp).width(200.dp).padding(bottom = 8.dp)
                        ) {
                        Text(text = "Upload", fontSize = 18.sp)
                    }
                }
            }
        }
    }
}