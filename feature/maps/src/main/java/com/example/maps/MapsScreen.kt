package com.example.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Geocoder
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.util.Locale

@SuppressLint("MissingPermission") // we only call this after explicit permission check
private fun fetchLastLocationImpl(
    fusedLocationClient: FusedLocationProviderClient,
    context: android.content.Context,
    onSuccess: (lat: Double, lng: Double, address: String) -> Unit,
    onNull: () -> Unit,
    onError: (Throwable) -> Unit
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude

                // Reverse geocode lat/lng → address
                val geocoder = Geocoder(context, Locale.getDefault())
                try {
                    val addresses = geocoder.getFromLocation(lat, lng, 1)
                    val address = if (addresses?.isNotEmpty() == true) {
                        addresses[0].getAddressLine(0) ?: "No address found"
                    } else {
                        "No address found"
                    }

                    onSuccess(lat, lng, address)
                } catch (e: Exception) {
                    // Geocoder failed → send coordinates anyway
                    onSuccess(lat, lng, "Address lookup failed")
                }
            } else {
                onNull()
            }
        }
        .addOnFailureListener { e ->
            onError(e)
        }
}
private fun bitmapDescriptorFromVector(
    context: Context,
    vectorResId: Int
): BitmapDescriptor {
    // Ensure the Maps SDK (and BitmapDescriptorFactory) is initialized
    try {
        MapsInitializer.initialize(context.applicationContext)
    } catch (e: Exception) {
        // If initialization fails (no Play Services, etc.), use default marker
        return BitmapDescriptorFactory.defaultMarker()
    }

    val drawable = ContextCompat.getDrawable(context, vectorResId)
        ?: return BitmapDescriptorFactory.defaultMarker()

    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

@Composable
fun MapsScreen() {
    val context = LocalContext.current

    // Initial map camera (Ajmer)
    val ajmer = LatLng(26.449896, 74.639915)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(ajmer, 17f)
    }

    // Location client
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // UI state
    var latitude by remember { mutableStateOf("—") }
    var longitude by remember { mutableStateOf("—") }
    var address by remember { mutableStateOf("—") }
    var status by remember { mutableStateOf("Idle") }
    // Run‑tracking state
    var isTracking by remember { mutableStateOf(false) }
    val trackPoints = remember { mutableStateListOf<LatLng>() }
    // Custom marker icon for current position
    val markerIcon = remember {
        bitmapDescriptorFromVector(
            context = context,
            vectorResId = R.drawable.baseline_location_pin_24
        )
    }
    // Marker state for current position (remembered once)
    val markerState = rememberMarkerState()

    // Helper: do we currently have location permission?
    fun hasLocationPermission(): Boolean {
        val hasFine = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarse = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return hasFine || hasCoarse
    }

    // High‑accuracy updates every ~2 seconds, min 5m between points
    val locationRequest = remember {
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2_000L // 2 seconds
        )
            .setMinUpdateDistanceMeters(5f)
            .build()
    }

    // Callback for continuous updates while tracking
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                for (location in result.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)

                    // Add new point to the polyline list
                    trackPoints.add(latLng)

                    // Optionally update text + camera while running
                    latitude = String.format("%.5f", location.latitude)
                    longitude = String.format("%.5f", location.longitude)

                    cameraPositionState.position =
                        CameraPosition.fromLatLngZoom(latLng, 17f)
                }
            }
        }
    }

    // Wrapper that calls the impl only if permission is granted
    fun fetchLastLocation() {
        if (!hasLocationPermission()) {
            status = "Missing location permission"
            return
        }
        fetchLastLocationImpl(
            fusedLocationClient = fusedLocationClient,
            context = context,
            onSuccess = { lat, lng, addr ->
                latitude = String.format("%.5f", lat)
                longitude = String.format("%.5f", lng)
                address = addr
                status = "Location updated"

                cameraPositionState.position =
                    CameraPosition.fromLatLngZoom(
                        LatLng(lat, lng),
                        16f
                    )
            },
            onNull = {
                status = "Location is null"
            },
            onError = { e ->
                status = "Location error: ${e.localizedMessage}"
            }
        )
    }
    fun startLocationUpdates() {
        if (!hasLocationPermission()) {
            status = "Missing location permission"
            return
        }

        // Reset previous run
        trackPoints.clear()
        status = "Tracking started..."
        isTracking = true

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        isTracking = false
        status = "Tracking stopped"
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (granted) {
            fetchLastLocation()
        } else {
            status = "Permission denied"
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .weight(1.4f)
                .fillMaxWidth()
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ){
                // Draw the route if we have at least 2 points
                if (trackPoints.size > 1) {
                    Polyline(
                        points = trackPoints,
                        color = androidx.compose.ui.graphics.Color.Red,
                        width = 10f
                    )
                }
                val lastPoint = trackPoints.lastOrNull()
                if (lastPoint != null) {
                    // Create the MarkerState once, then update its position whenever lastPoint changes
                    markerState.position = lastPoint
                    Marker(
                        state = markerState,
                        icon = markerIcon,
                        title = "Current position"
                    )
                }
            }
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
            // Start Run button
            Button(
                onClick = {
                    if (hasLocationPermission()) {
                        startLocationUpdates()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                enabled = !isTracking,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    contentColor = MaterialTheme.colorScheme.surfaceContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = if (isTracking) "Tracking..." else "Start Run",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.headlineSmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Stop Run button
            Button(
                onClick = { stopLocationUpdates() },
                enabled = isTracking,
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onSecondary
                )
            ) {
                Text(
                    text = "Stop Run",
                    fontSize = 15.sp,
                    style = MaterialTheme.typography.headlineSmall
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Latitude: $latitude , Longitude: $longitude",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 18.sp
            )
            Text(text = "Address: $address", textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Status: $status", style = MaterialTheme.typography.headlineSmall,
                fontSize = 18.sp)
        }
    }
}
