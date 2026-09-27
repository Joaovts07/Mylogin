package com.example.loginlib.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginlib.data.repository.AuthRepository
import com.example.loginlib.data.repository.AuthRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginState {
    data object Loading : LoginState
    data object Logged : LoginState
    data object Logout : LoginState
    data class Error(val message: String) : LoginState
}

/**
 * App-level auth state. Host apps observe [loginState] to switch between
 * [com.example.loginlib.ui.navigation.LoginNavigation] and their own content.
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _loginState.value = if (firebaseAuth.currentUser != null) LoginState.Logged else LoginState.Logout
    }

    init {
        auth.addAuthStateListener(authListener)
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout().onFailure { e ->
                _loginState.value = LoginState.Error(e.message ?: "Erro ao sair")
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
    }
}
