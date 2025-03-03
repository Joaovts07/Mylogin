package com.example.login.firebase

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit

val auth: FirebaseAuth = Firebase.auth

sealed class PhoneAuthState {
    object Default : PhoneAuthState()
    object Loading : PhoneAuthState()
    data class Success(val user: FirebaseUser) : PhoneAuthState()
    data class Error(val message: String) : PhoneAuthState()
    data class CodeSent(val verificationId: String) : PhoneAuthState()
}
@Composable
fun PhoneAuthentication(
    activity: Activity,
    phoneNumber: String,
    verificationCode: String? = null,
    verificationId: String = "",
    onAuthStateChanged: (PhoneAuthState) -> Unit
) {
    var authState by remember { mutableStateOf<PhoneAuthState>(PhoneAuthState.Default) }
    var resendToken by remember { mutableStateOf<PhoneAuthProvider.ForceResendingToken?>(null) }

    fun signInWithPhoneAuthCredential(activity: Activity, credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        authState = PhoneAuthState.Success(it)
                    }
                } else {
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        authState = PhoneAuthState.Error("Erro autenticação: ${task.exception?.message}")
                    }
                }
            }
    }
    verificationCode?.let {
        val credential = PhoneAuthProvider.getCredential(verificationId, verificationCode)
        signInWithPhoneAuthCredential(activity, credential)
    }

    val startVerification = {
        authState = PhoneAuthState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(activity, credential)
                authState = PhoneAuthState.Success(auth.currentUser!!)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)
                authState = when (e) {
                    is FirebaseAuthInvalidCredentialsException ->
                        PhoneAuthState.Error("Número de telefone inválido.")
                    is FirebaseTooManyRequestsException ->
                        PhoneAuthState.Error("O limite de SMS foi atingido.")
                    else -> PhoneAuthState.Error("Falha na verificação.")
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                resendToken = token
                authState = PhoneAuthState.CodeSent(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    if (authState is PhoneAuthState.Default) {
        startVerification()
    }

    onAuthStateChanged(authState)

   /* // Função para reenviar o código
    val resendCode = {
        if (resendToken != null) {
            authState = PhoneAuthState.Loading
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .setForceResendingToken(resendToken!!)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }*/
}
