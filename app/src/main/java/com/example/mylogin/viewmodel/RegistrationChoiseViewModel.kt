package com.example.mylogin.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.loginlib.validators.isValidEmail
import com.example.loginlib.validators.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.TimeUnit

data class RegistrationChoiseUiState(
    val method: String = "email",
    val email: String = "",
    val password: String = "",
    val phoneNumber: String = "",
    val phoneNumberError: Boolean = false,
    val showSnackbar: Boolean = false,
    val snackbarMessage: String = ""
)

class RegistrationChoiseViewModel(
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationChoiseUiState())
    val uiState: StateFlow<RegistrationChoiseUiState> = _uiState.asStateFlow()

    fun onMethodChange(value: String) {
        _uiState.update { it.copy(method = value) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun onPhoneNumberChange(value: String) {
        if (value.length <= 15) {
            _uiState.update {
                it.copy(
                    phoneNumber = value.filter { char ->
                        char.isDigit() || char == '(' || char == ')' || char == '-' || char == ' '
                    }
                )
            }
        }
    }

    private fun signInWithPhoneAuthCredential(activity: Activity, credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    _uiState.update { it.copy(showSnackbar = true, snackbarMessage = "Login bem sucedido") }
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        _uiState.update {
                            it.copy(showSnackbar = true, snackbarMessage = "O código de verificação era inválido.")
                        }
                    }
                }
            }
    }

    fun submitEmail(onNavigateConfirmation: (email: String) -> Unit, onNavigateConfirmationSms: (phoneNumber: String) -> Unit) {
        val state = _uiState.value
        if (validateWithEmail(state.email, state.password)) return

        auth.createUserWithEmailAndPassword(state.email, state.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Salvar dados adicionais do usuário
                    // ...
                    auth.currentUser?.sendEmailVerification()
                        ?.addOnCompleteListener { verificacaoTask ->
                            if (verificacaoTask.isSuccessful) {
                                onNavigateConfirmation(state.email)
                            } else {
                                onNavigateConfirmationSms(state.phoneNumber)
                            }
                        }
                } else {
                    // Exibir mensagem de erro
                }
            }
    }

    fun submitPhone(activity: Activity, onCodeSent: (phoneNumber: String, verificationId: String, resendToken: String) -> Unit) {
        val state = _uiState.value

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(activity, credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)
                _uiState.update { it.copy(showSnackbar = true) }
                if (e is FirebaseAuthInvalidCredentialsException) {
                    _uiState.update { it.copy(snackbarMessage = "Número de telefone inválido.") }
                } else if (e is FirebaseTooManyRequestsException) {
                    _uiState.update { it.copy(snackbarMessage = "O limite de SMS foi atingido.") }
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                val storedVerificationId = verificationId
                val resendToken = token

                val phoneNumber = state.phoneNumber.filter { it.isDigit() }
                onCodeSent(phoneNumber, storedVerificationId, resendToken.toString())
            }
        }

        val phoneNumber = "+55${state.phoneNumber.filter { it.isDigit() }}"
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun validateWithEmail(email: String, password: String): Boolean {
        val isEmailError1 = !isValidEmail(email)
        val isPasswordError1 = !isValidPassword(password)
        return isEmailError1 || isPasswordError1
    }
}
