package com.example.mylogin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.loginlib.validators.isValidBirthDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RegistrationBasicUiState(
    val fullName: String = "",
    val birthDate: String = "",
    val birthDateError: Boolean = false
)

class RegistrationBasicViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationBasicUiState())
    val uiState: StateFlow<RegistrationBasicUiState> = _uiState.asStateFlow()

    fun onFullNameChange(value: String) {
        _uiState.update { it.copy(fullName = value) }
    }

    fun onBirthDateChange(value: String) {
        if (value.filter { it.isDigit() }.length <= 8) {
            _uiState.update { it.copy(birthDate = value.filter { char -> char.isDigit() }) }
        }
    }

    fun onNextClick(onNavigate: (fullName: String, birthDate: String) -> Unit) {
        val state = _uiState.value
        val birthDateError = !isValidBirthDate(state.birthDate)
        _uiState.update { it.copy(birthDateError = birthDateError) }
        if (!birthDateError) {
            onNavigate(state.fullName, state.birthDate)
        }
    }
}
