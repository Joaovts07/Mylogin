package com.example.loginlib.data.repository

import kotlinx.datetime.LocalDate

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun createUser(name: String, email: String, dateBirthday: LocalDate?): Result<Unit>
    suspend fun checkIfUserExists(userId: String? = null): Boolean
}
