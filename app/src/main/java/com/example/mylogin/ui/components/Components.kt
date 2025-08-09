package com.example.mylogin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mylogin.R
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
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit
) {
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    fun validatePassword(password: String) {
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
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        supportingText = {
            if (isPasswordError) {
                Text(
                    "Senha deve ter pelo menos 6 caracteres",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        trailingIcon = {
            val image = if (isPasswordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (isPasswordVisible) "Ocultar senha" else "Mostrar senha"

            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )
}

@Composable
fun GoogleSignInButton(onClick:() -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = "Google Sign In",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Entrar com Google", color = Color.Black, fontSize = 16.sp)
    }
}