package com.example.mylogin

import RegistrationBasicScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview  
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mylogin.ui.ConfirmationScreen
import com.example.mylogin.ui.LoginScreen
import com.example.mylogin.ui.RegistrationChoiseScreen
import com.example.mylogin.ui.theme.MyLoginTheme

public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MyLoginTheme {
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
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
                    composable("confirmation/{verificationType}/{email?}/{phoneNumber?}") { backStackEntry ->
                        val verificationType = backStackEntry.arguments?.getString("verificationType") ?: "email"
                        val email = backStackEntry.arguments?.getString("email")
                        val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")

                        ConfirmationScreen(
                            verificationType = verificationType,
                            email = email,
                            phoneNumber = phoneNumber,
                            onResendClick = {
                                // Lógica para reenviar email ou SMS
                            }
                        )
                    }
                }
            }
        }
    }
}    

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    val navController = rememberNavController()
    MyLoginTheme {
        LoginScreen(navController)
    }
}