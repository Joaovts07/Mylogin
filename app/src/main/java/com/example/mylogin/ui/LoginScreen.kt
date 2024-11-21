package com.example.mylogin.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.mylogin.ui.components.EmailInput
import com.example.mylogin.ui.components.PasswordInput
import com.example.mylogin.validators.isValidEmail
import com.example.mylogin.validators.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    fun signInWithEmailAndPassword(email: String, password: String, onResult: (Boolean) -> Unit) {
        val auth: FirebaseAuth = Firebase.auth

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    Scaffold(
        snackbarHost = {
            if (showSnackbar) {
                Snackbar { Text(snackbarMessage) }
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
                email = email,
                onEmailChange = { email = it },
            )
            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                password = password,
                onPasswordChange = { password = it },
            )
            Spacer(modifier = Modifier.height(24.dp))

            if (showError) {
                Text(
                    "Email ou senha inválidos",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    isEmailError = !isValidEmail(email)
                    isPasswordError = !isValidPassword(password)
                    if (isEmailError || isPasswordError) {
                        return@Button
                    }

                    signInWithEmailAndPassword(email, password) { success ->
                        if (success) {
                            showSnackbar = true
                            snackbarMessage = "Login bem-sucedido!"
                            showError = false
                        } else {
                            showError = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(16.dp))

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


