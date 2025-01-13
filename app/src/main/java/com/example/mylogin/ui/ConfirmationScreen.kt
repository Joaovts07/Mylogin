package com.example.mylogin.ui

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.login.firebase.PhoneAuthState
import com.example.login.firebase.PhoneAuthentication
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmationScreen(
    navController: NavController,
    verificationType: String,
    id: String,
    verificationId: String
) {
    val auth: FirebaseAuth = Firebase.auth
    val context = LocalContext.current
    val activity = context as Activity
    var timeLeft by remember { mutableIntStateOf(60) }
    var resendEnabled by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }
    var showPhoneAuthentication by remember { mutableStateOf(false) }

    @Composable
    fun verifySms(
        formatedPhone: String,
    ) {
        PhoneAuthentication(activity, formatedPhone, verificationCode, verificationId) { state ->
            when (state) {
                is PhoneAuthState.CodeSent -> {

                }
                is PhoneAuthState.Error -> {
                    Log.i("error", state.message)
                }
                PhoneAuthState.Default -> { }
                PhoneAuthState.Loading -> { }
                is PhoneAuthState.Success -> {
                    Log.i("success", "Login bem sucedido:${state.user}")
                }
            }
        }

    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Confirmation") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(key1 = Unit) {
                while (timeLeft > 0) {
                    delay(1.seconds)
                    timeLeft -= 1
                }
                resendEnabled = true
            }

            if (verificationType == "email") {
                Text(
                    text = "A confirmation email has been sent to:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = id ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Please check your inbox and click the link to confirm your email.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Uma mensagem SMS com um código de verificação foi enviada para:",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = id ?: "",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,

                    )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Insira o código de verificação abaixo:",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = {
                        if (it.length < 7) {
                            verificationCode = it
                        }
                        if (it.length == 6) {
                            showPhoneAuthentication = true
                        }
                    },
                    label = { Text("Código") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),

                    )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(
                    onClick = { onResendClick(auth, (verificationType == "email"), activity, id) },
                    enabled = resendEnabled
                ) {
                    Text(if (resendEnabled) "Reenviar" else "Reenviar ($timeLeft)")
                }
                Button(onClick = { navController.popBackStack() }) {
                    Text("Voltar")
                }
            }
        }
        if (showPhoneAuthentication ) {
            verifySms(
                formatedPhone = "+55${id.filter { it.isDigit() }}"
            )
        }
    }
}

fun onResendClick(auth: FirebaseAuth,isEmail: Boolean, activity: Activity, phoneNumber: String? ) {
    if (isEmail) {
        auth.currentUser?.sendEmailVerification()
    } else {
        if (phoneNumber != null) {
            //phoneAuthentication(activity, phoneNumber)
        }
    }
}