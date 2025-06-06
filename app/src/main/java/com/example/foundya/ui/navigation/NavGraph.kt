package com.example.foundya.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.foundya.ui.auth.LoginScreen
import com.example.foundya.ui.auth.RegisterScreen
import com.example.foundya.ui.main.MainScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = if (FirebaseAuth.getInstance().currentUser != null) "main" else "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { navController.navigate("main") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack()
                    navController.navigate("main")
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable("main") {
            MainScreen()
        }

    }
}