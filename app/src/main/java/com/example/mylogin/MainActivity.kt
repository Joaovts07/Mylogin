package com.example.mylogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.loginlib.ui.navigation.LoginNavigation
import com.example.loginlib.viewmodel.AuthViewModel
import com.example.loginlib.viewmodel.LoginState
import com.example.mylogin.ui.theme.MyLoginTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyLoginTheme {
                val authViewModel: AuthViewModel = viewModel { AuthViewModel() }
                val loginState by authViewModel.loginState.collectAsStateWithLifecycle()
                when (loginState) {
                    LoginState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    LoginState.Logged -> Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Logado")
                        Button(onClick = authViewModel::logout) { Text("Sair") }
                    }
                    LoginState.Logout, is LoginState.Error -> LoginNavigation(
                        navController = rememberNavController(),
                        serverClientId = getString(R.string.default_web_client_id)
                    )
                }
            }
        }
    }
}
