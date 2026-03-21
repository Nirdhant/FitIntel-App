package com.example.authentication.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.authentication.ui.LoginScreen
import com.example.authentication.ui.SignUpScreen
import com.example.navigation.Screen

fun NavGraphBuilder.authNavGraph(navController: NavController) {
    composable(Screen.Login.route) {
        LoginScreen(onSignUpClick = {
            navController.navigate(Screen.SignUp.route){
                popUpTo(Screen.Login.route){inclusive =true}
            }
        },
         navController = navController
        )
    }
    composable(Screen.SignUp.route) {
        SignUpScreen(onLoginClick = {
            navController.navigate(Screen.Login.route){
                popUpTo(Screen.SignUp.route){inclusive=true}
            }
        },
            navController = navController
        )
    }
}
