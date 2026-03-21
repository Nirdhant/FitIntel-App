package com.example.fitintel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.authentication.navigation.authNavGraph
import com.example.navigation.Screen
import com.example.ui.theme.FitIntelTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitIntelTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FitIntelNavigation()
                }
            }
        }
    }
}

@Composable
fun FitIntelNavigation() {
    val navController = rememberNavController()

    val startDestination =
        if (FirebaseAuth.getInstance().currentUser != null) {
            Screen.Main.route
        } else {
            Screen.Login.route
        }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth graph from :authentication
        authNavGraph(navController)

        // Main app (with bottom navigation) – we will navigate here after login/signup
        composable(Screen.Main.route) {
            MainScreen(onLogout = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Main.route) { inclusive = true }
                }
            })
        }
    }
}
