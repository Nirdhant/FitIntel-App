package com.example.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object SignUp : Screen("signup")

    // Main app (bottom nav host)
    object Main : Screen("main")

    // Bottom nav
    object Home : Screen("main/home")
    object Pdf : Screen("main/pdf")
    object Gemini : Screen("main/gemini")
    object Maps : Screen("main/maps")
}
