package com.example.mylogin.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

data class ConfirmationUiState(
    val timeLeft: Int = 60,
    val resendEnabled: Boolean = false,
    val verificationCode: String = "",
    val showPhoneAuthentication: Boolean = false
)

class ConfirmationViewModel(
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfirmationUiState())
    val uiState: StateFlow<ConfirmationUiState> = _uiState.asStateFlow()

    private var countdownStarted = false

    fun startCountdown() {
        if (countdownStarted) return
        countdownStarted = true
        viewModelScope.launch {
            while (_uiState.value.timeLeft > 0) {
                delay(1.seconds)
                _uiState.update { it.copy(timeLeft = it.timeLeft - 1) }
            }
            _uiState.update { it.copy(resendEnabled = true) }
        }
    }

    fun onVerificationCodeChange(value: String) {
        if (value.length < 7) {
            _uiState.update { it.copy(verificationCode = value) }
        }
        if (value.length == 6) {
            _uiState.update { it.copy(showPhoneAuthentication = true) }
        }
    }

    fun onResendClick(isEmail: Boolean, activity: Activity, phoneNumber: String?) {
        if (isEmail) {
            auth.currentUser?.sendEmailVerification()
        } else {
            if (phoneNumber != null) {
                //phoneAuthentication(activity, phoneNumber)
            }
        }
    }
}
