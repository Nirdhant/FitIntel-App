package com.example.fitintel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gemini.GeminiScreen
import com.example.home.HomeScreen
import com.example.maps.MapsScreen
import com.example.navigation.Screen
import com.example.pdf.PdfScreen

data class BottomNavItem(
    val screen: Screen,
    val icon: Int,
    val label: String
)

@Composable
fun MainScreen(onLogout: ()->Unit={}) {
    // This NavController is ONLY for the 4 bottom‑nav destinations
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem(Screen.Home,R.drawable.dashboard, "Home"),
        BottomNavItem(Screen.Pdf, R.drawable.upload,"Upload Pdf"),
        BottomNavItem(Screen.Gemini, com.example.gemini.R.drawable.gemini,"Report"),
        BottomNavItem(Screen.Maps,R.drawable.map, "Track")
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                modifier = Modifier.shadow(elevation = 50.dp,shape = RoundedCornerShape(20.dp),)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentDestination.isCurrentDestination(item.screen.route),
                        onClick = {
                            navController.navigate(item.screen.route) {
                                // Standard bottom‑nav behavior
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        // Using simple text as "icon" to avoid extra icon dependencies for now
                        icon = {
                            Image(
                                painter = painterResource(item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(30.dp)
                            )
                        },
                        label = {
                            Text(item.label,style = MaterialTheme.typography.bodyLarge,
                                fontSize = 12.sp)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onLogoutClick = onLogout)
            }
            composable(Screen.Pdf.route) {
                PdfScreen()
            }
            composable(Screen.Gemini.route) {
                GeminiScreen()
            }
            composable(Screen.Maps.route) {
                MapsScreen()
            }
        }
    }
}

private fun NavDestination?.isCurrentDestination(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
