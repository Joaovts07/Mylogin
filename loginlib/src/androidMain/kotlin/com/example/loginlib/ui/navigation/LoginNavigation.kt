package com.example.loginlib.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.loginlib.ui.ConfirmationScreen
import com.example.loginlib.ui.LoginScreen
import com.example.loginlib.ui.RegistrationBasicScreen
import com.example.loginlib.ui.RegistrationChoiseScreen

/**
 * Login and registration flow. It uses the host's MaterialTheme.
 * Observe [com.example.loginlib.viewmodel.AuthViewModel.loginState] to leave this flow once the user is logged in;
 * [onLoginSuccess] is only a hook for extra work after a successful login.
 *
 * @param serverClientId the OAuth Web client ID used for Google Sign-In.
 */
@Composable
fun LoginNavigation(
    navController: NavHostController,
    serverClientId: String,
    onLoginSuccess: () -> Unit = {}
) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                navController = navController,
                serverClientId = serverClientId,
                onLoginSuccess = onLoginSuccess
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
