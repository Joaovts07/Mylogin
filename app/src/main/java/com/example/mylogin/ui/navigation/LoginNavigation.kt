package com.example.mylogin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mylogin.ui.ConfirmationScreen
import com.example.mylogin.ui.LoginScreen
import com.example.mylogin.ui.RegistrationBasicScreen
import com.example.mylogin.ui.RegistrationChoiseScreen
import com.example.mylogin.ui.theme.MyLoginTheme

@Composable
fun LoginNavigation(navController: NavHostController, routeSuccess: String, onLoginSuccess: @Composable () -> Unit) {
    MyLoginTheme {
        NavHost(navController = navController, startDestination = "login") {
            composable(routeSuccess) {
                onLoginSuccess()
            }
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginSuccess = {
                        // Navegar para a rota desejada após o login
                        navController.navigate(routeSuccess) {
                            popUpTo("login") { inclusive = true } // Remover a tela de login da pilha
                        }
                    }
                )
            }
            composable("basicForm") { RegistrationBasicScreen(navController) }
            composable(
                "choiseForm/{nome}/{dataNascimento}",
                arguments = listOf(
                    navArgument("nome") { type = NavType.StringType },
                    navArgument("dataNascimento") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                RegistrationChoiseScreen(
                    navController,
                    backStackEntry.arguments?.getString("nome") ?: "",
                    backStackEntry.arguments?.getString("dataNascimento") ?: ""
                )

            }
            composable("confirmationScreen/{verificationType}/{id}/{verificationId}") { backStackEntry ->
                val verificationType = backStackEntry.arguments?.getString("verificationType") ?: "email"
                val id = backStackEntry.arguments?.getString("id") ?: ""
                val verificationId = backStackEntry.arguments?.getString("verificationId") ?: ""
                ConfirmationScreen(
                    navController,
                    verificationType = verificationType,
                    id = id,
                    verificationId = verificationId
                )
            }
        }
    }
}
