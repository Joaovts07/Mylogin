package com.example.mylogin.ui

import android.app.Activity
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.loginlib.components.EmailInput
import com.example.loginlib.components.PasswordInput
import com.example.loginlib.validators.PhoneNumberMaskTransformation
import com.example.mylogin.viewmodel.RegistrationChoiseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationChoiseScreen(navController: NavController, nome: String, dataNascimento: String) {
    val viewModel: RegistrationChoiseViewModel = viewModel { RegistrationChoiseViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as Activity

    Scaffold(
        snackbarHost = {
            if (uiState.showSnackbar) {
                Snackbar { Text(uiState.snackbarMessage) }
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
                    selected = uiState.method == "email",
                    onClick = { viewModel.onMethodChange("email") }
                )
                Text("Email")
                Spacer(modifier = Modifier.width(16.dp))

                RadioButton(
                    selected = uiState.method == "telefone",
                    onClick = { viewModel.onMethodChange("telefone") }
                )
                Text("Telefone")
            }
            Spacer(modifier = Modifier.height(24.dp))


            if (uiState.method == "email") {
                EmailInput(
                    email = uiState.email,
                    onEmailChange = viewModel::onEmailChange,
                )
                PasswordInput(
                    password = uiState.password,
                    onPasswordChange = viewModel::onPasswordChange,
                )

            } else {
                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = viewModel::onPhoneNumberChange,
                    label = { Text("Phone Number") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PhoneNumberMaskTransformation(),
                    isError = uiState.phoneNumberError,
                    supportingText = {
                        if (uiState.phoneNumberError) {
                            Text("Invalid phone number")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (uiState.method == "email") {
                        viewModel.submitEmail(
                            onNavigateConfirmation = { email ->
                                navController.navigate("confirmationScreen/${email}")
                            },
                            onNavigateConfirmationSms = { phoneNumber ->
                                navController.navigate("confirmation/sms/${phoneNumber}")
                            }
                        )
                    } else {
                        viewModel.submitPhone(activity) { phoneNumber, verificationId, resendToken ->
                            navController.navigate(
                                "confirmation/sms/${phoneNumber}/${verificationId}/{${resendToken}}"
                            )
                        }
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
