package com.example.mylogin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.loginlib.components.LoadingButton
import com.example.loginlib.components.EmailInput
import com.example.loginlib.components.PasswordInput
import com.example.mylogin.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, onLoginSuccess: () -> Unit) {
    val viewModel: LoginViewModel = viewModel { LoginViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = {
            if (uiState.showSnackbar) {
                Snackbar { Text(uiState.snackbarMessage) }
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Login") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            EmailInput(
                email = uiState.email,
                onEmailChange = viewModel::onEmailChange,
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                password = uiState.password,
                onPasswordChange = viewModel::onPasswordChange,
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.showError) {
                Text(
                    "Email ou senha inválidos",
                    color = MaterialTheme.colorScheme.error

                )
            }
            LoadingButton(
                onClick = {
                    viewModel.login(
                        onLoginSuccess = onLoginSuccess,
                        onNeedsVerification = { email, verificationId ->
                            navController.navigate("confirmationScreen/email/${email}/${verificationId}")
                        }
                    )
                },
                isLoading = uiState.isLoading,
                text = "Login",
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Não tem uma conta? Cadastre-se",
                modifier = Modifier
                    .clickable { navController.navigate("basicForm") }
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
