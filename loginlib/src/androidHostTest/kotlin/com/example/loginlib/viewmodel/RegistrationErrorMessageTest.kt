package com.example.loginlib.viewmodel

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlin.test.Test
import kotlin.test.assertEquals

class RegistrationErrorMessageTest {

    @Test
    fun emailAlreadyInUse() {
        val e = FirebaseAuthUserCollisionException("ERROR_EMAIL_ALREADY_IN_USE", "in use")
        assertEquals("Este email já está cadastrado. Entre com Google ou faça login.", registrationErrorMessage(e))
    }

    @Test
    fun weakPasswordWinsOverInvalidCredentials() {
        val e = FirebaseAuthWeakPasswordException("ERROR_WEAK_PASSWORD", "weak", "too short")
        assertEquals("Senha fraca: use pelo menos 6 caracteres.", registrationErrorMessage(e))
    }

    @Test
    fun invalidEmail() {
        val e = FirebaseAuthInvalidCredentialsException("ERROR_INVALID_EMAIL", "bad email")
        assertEquals("Email inválido.", registrationErrorMessage(e))
    }

    @Test
    fun networkError() {
        assertEquals("Sem conexão. Tente novamente.", registrationErrorMessage(FirebaseNetworkException("offline")))
    }

    @Test
    fun unknownOrMissingError() {
        assertEquals("Não foi possível concluir o cadastro.", registrationErrorMessage(IllegalStateException()))
        assertEquals("Não foi possível concluir o cadastro.", registrationErrorMessage(null))
    }
}
