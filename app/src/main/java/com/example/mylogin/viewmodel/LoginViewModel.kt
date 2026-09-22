package com.example.mylogin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginlib.data.repository.AuthRepository
import com.example.loginlib.data.repository.AuthRepositoryImpl
import com.example.loginlib.validators.isValidEmail
import com.example.loginlib.validators.isValidPassword
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val showError: Boolean = false,
    val showSnackbar: Boolean = false,
    val snackbarMessage: String = "",
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value) }
    }

    fun login(
        onLoginSuccess: () -> Unit,
        onNeedsVerification: (email: String, verificationId: String) -> Unit
    ) {
        val email = _uiState.value.email
        val password = _uiState.value.password

        _uiState.update { it.copy(isLoading = true) }

        if (verifyFields(email, password)) {
            _uiState.update { it.copy(isLoading = false, showError = true) }
            return
        }

        viewModelScope.launch {
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                if (auth.currentUser?.isEmailVerified == true) {
                    _uiState.update { it.copy(showSnackbar = true, showError = false) }
                    onLoginSuccess()
                } else {
                    val verificationId = ""
                    auth.currentUser?.sendEmailVerification()
                    onNeedsVerification(email, verificationId)
                }
            } else {
                _uiState.update { it.copy(showError = true, isLoading = false) }
            }
        }
    }

    private fun verifyFields(email: String, password: String): Boolean {
        val isEmailError = !isValidEmail(email)
        val isPasswordError = !isValidPassword(password)
        return isEmailError || isPasswordError
    }
}
