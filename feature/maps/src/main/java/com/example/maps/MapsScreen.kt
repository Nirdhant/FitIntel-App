package com.example.maps

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng



@Composable
fun MapsScreen() {
    val ajmer = LatLng(26.449896, 74.639915)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ajmer, 12f)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top half: real map
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            )

        }

        // Bottom half: controls + info
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Button(onClick = { /* TODO: later: get location / track */ }) {
                Text(text = "Get location")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Latitude: —", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Longitude: —", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Address: —", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: Not tracking", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
