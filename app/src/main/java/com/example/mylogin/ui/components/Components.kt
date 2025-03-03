package com.example.mylogin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.mylogin.validators.isValidEmail
import com.example.mylogin.validators.isValidPassword

@Composable
fun EmailInput(email: String, onEmailChange: (String) -> Unit) {
    var isEmailError by remember { mutableStateOf(false) }

    fun validateEmail(email: String) {
        if (email.isNotEmpty()) {
            isEmailError = !isValidEmail(email)
        }
    }

    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = email,
        onValueChange = {
            validateEmail(it)
            onEmailChange(it)
        },
        label = { Text("Email") },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        supportingText = {
            if (isEmailError) {
                Text("Email inválido", color = MaterialTheme.colorScheme.error)
            }
        }
    )
}

@Composable
fun PasswordInput(password: String, onPasswordChange: (String) -> Unit) {

    var isPasswordError by remember { mutableStateOf(false) }

    fun validatePassword(password:String) {
        if (password.isNotEmpty()) {
            isPasswordError = !isValidPassword(password)
        }
    }

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = password,
        onValueChange = {
            validatePassword(it)
            onPasswordChange(it)
        },
        label = { Text("Senha") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        supportingText = {
            if (isPasswordError) {
                Text("Senha deve ter pelo menos 6 caracteres",
                    color = MaterialTheme.colorScheme.error
                )

            }
        }
    )
}