package com.example.mylogin.ui

import android.app.Activity
import android.util.Log
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
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.mylogin.ui.components.EmailInput
import com.example.mylogin.ui.components.PasswordInput
import com.example.mylogin.validators.PhoneNumberMaskTransformation
import com.example.mylogin.validators.isValidEmail
import com.example.mylogin.validators.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationChoiseScreen(navController: NavController, nome: String, dataNascimento: String) {
    var method by remember { mutableStateOf("email") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    val activity = context as Activity

    // Função para fazer login com a credencial
    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                // Login bem-sucedido
                val user = task.result?.user
                showSnackbar =  true
                snackbarMessage = "Login bem sucedido"
                // ...
            } else {
            // Login falhou
            Log.w("TAG", "signInWithCredential:failure", task.exception)
            if (task.exception is FirebaseAuthInvalidCredentialsException) {
                showSnackbar =  true
                snackbarMessage = "O código de verificação era inválido."
            }
            // Atualiza a UI
        }
        }
    }

    // Callbacks para lidar com o código de verificação
    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Essa função é chamada quando a verificação é concluída automaticamente,
            // como em alguns dispositivos com o Google Play Services.
            // Você pode usar a credencial para fazer login do usuário diretamente.
            signInWithPhoneAuthCredential(credential) // Função para fazer login
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Essa função é chamada quando a verificação falha, por exemplo,
            // número de telefone inválido, limite de SMS atingido, etc.
            // Exiba uma mensagem de erro para o usuário.
            Log.w("TAG", "onVerificationFailed", e)
            showSnackbar = true
            if (e is FirebaseAuthInvalidCredentialsException) {
                snackbarMessage = "Número de telefone inválido."
            } else if (e is FirebaseTooManyRequestsException) {
                snackbarMessage = "O limite de SMS foi atingido."
            }

            // Mostra uma mensagem de erro para o usuário
            //showErrorSnackbar("Verification failed: ${e.message}")
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken) {
            // Essa função é chamada quando o código de verificação é enviado por SMS.
            // Salve o verificationId e navegue para a tela de confirmação de SMS.
            val storedVerificationId = verificationId
            val resendToken = token

            val phoneNumber = phoneNumber.filter { it.isDigit() } // Remove a máscara
            navController.navigate(
                "confirmation/sms/${phoneNumber}/${storedVerificationId}/{$resendToken}")
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
                var phoneNumberError by remember { mutableStateOf(false) }

                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        if (it.length <= 15) {  // Limita o número de caracteres
                            phoneNumber = it.filter { char -> char.isDigit() || char == '(' || char == ')' || char == '-' || char == ' ' }
                        }
                    },
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PhoneNumberMaskTransformation(),
                    isError = phoneNumberError,
                    supportingText = {
                        if (phoneNumberError) {
                            Text("Invalid phone number")
                        }
                    }
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
                                                navController.navigate("confirmationScreen/${email}")
                                            } else {
                                                navController.navigate("confirmation/sms/${phoneNumber}") // Para SMS
                                            }
                                        }
                                } else {
                                    // Exibir mensagem de erro
                                }
                            }
                    } else {
                        val phoneNumber = "+55${phoneNumber.filter { it.isDigit() }}" // Ajustar para formato E.164
                        val options = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(activity) // Passar a Activity atual
                            .setCallbacks(callbacks) // Definir callbacks para lidar com o código de verificação
                            .build()
                        PhoneAuthProvider.verifyPhoneNumber(options)
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





