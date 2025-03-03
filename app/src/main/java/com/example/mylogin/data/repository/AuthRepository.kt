package com.example.login.data.repository

import com.google.firebase.Timestamp

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun createUser(name: String, email: String, dateBirthday: Timestamp?): Result<Unit>
    suspend fun checkIfUserExists(userId: String? = null): Boolean
}
