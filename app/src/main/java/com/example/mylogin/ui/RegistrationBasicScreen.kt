package com.example.mylogin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.loginlib.validators.DateMaskTransformation
import com.example.mylogin.viewmodel.RegistrationBasicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBasicScreen(navController: NavController) {
    val viewModel: RegistrationBasicViewModel = viewModel { RegistrationBasicViewModel() }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Registration - Step 1") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.birthDate,
                onValueChange = viewModel::onBirthDateChange,
                label = { Text("Birth Date (dd/MM/yyyy)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = DateMaskTransformation(),
                isError = uiState.birthDateError,
                supportingText = {
                    if (uiState.birthDateError) {
                        Text("Invalid date or age under 18.")
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        viewModel.onNextClick { fullName, birthDate ->
                            navController.navigate("choiseForm/${fullName}/${birthDate}")
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Next")
                }
            }
        }
    }
}
