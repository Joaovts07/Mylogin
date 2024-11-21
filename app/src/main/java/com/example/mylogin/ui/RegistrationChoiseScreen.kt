package com.example.mylogin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylogin.ui.components.EmailInput
import com.example.mylogin.ui.components.PasswordInput
import com.example.mylogin.validators.isValidEmail
import com.example.mylogin.validators.isValidPassword
import com.example.mylogin.validators.isValidPhoneNumber
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationChoiseScreen(navController: NavController, nome: String, dataNascimento: String) {
    var method by remember { mutableStateOf("email") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    val auth: FirebaseAuth = Firebase.auth

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cadastro - Passo 2") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Escolha o método de validação:")
            Spacer(modifier = Modifier.height(16.dp))

            Row {
                RadioButton(
                    selected = method == "email",
                    onClick = { method = "email" }
                )
                Text("Email")
                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = method == "telefone",
                    onClick = { method = "telefone" }
                )
                Text("Telefone")
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (method == "email") {
                EmailInput(
                    email = email,
                    onEmailChange = { email = it },
                )
                PasswordInput(
                    password = password,
                    onPasswordChange = { password = it },
                )

            } else {
                OutlinedTextField(
                    value = telephone,
                    onValueChange = { isValidPhoneNumber(telephone); telephone = it },
                    label = { Text("Telefone") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (method == "email") {
                        if (validateWithEmail(email, password)) return@Button
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Salvar dados adicionais do usuário
                                    // ...
                                    auth.currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener { verificacaoTask ->
                                            if (verificacaoTask.isSuccessful) {
                                                // Navegar para a tela de confirmação
                                                navController.navigate("confirmacao_email")
                                            } else {
                                                // Exibir mensagem de erro
                                            }
                                        }
                                } else {
                                    // Exibir mensagem de erro
                                }
                            }
                    } else {
                        // TODO: Implementar lógica de cadastro com telefone
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Concluir Cadastro")
            }
        }
    }

}
private fun validateWithEmail(
    email: String,
    password: String
): Boolean {
    val isEmailError1: Boolean = !isValidEmail(email)
    val isPasswordError1: Boolean = !isValidPassword(password)
    return isEmailError1 || isPasswordError1
}


