package com.example.loginlib.viewmodel

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginUiStateTest {

    @Test
    fun idleIsNotBusy() {
        assertFalse(LoginUiState().isBusy)
    }

    @Test
    fun emailSignInIsBusyWithoutTheGoogleSpinner() {
        val state = LoginUiState(isLoading = true)
        assertTrue(state.isBusy)
        assertFalse(state.isGoogleLoading)
    }

    @Test
    fun googleSignInIsBusyWithoutTheEmailSpinner() {
        val state = LoginUiState(isGoogleLoading = true)
        assertTrue(state.isBusy)
        assertFalse(state.isLoading)
    }
}
