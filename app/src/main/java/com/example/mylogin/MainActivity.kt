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
import com.example.mylogin.ui.LoginScreen
import com.example.mylogin.ui.RegistrationChoiseScreen
import com.example.mylogin.ui.theme.MyLoginTheme

class MainActivity : ComponentActivity() {
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