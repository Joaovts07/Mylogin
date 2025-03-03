package com.example.login.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun loginWithGoogle(idToken: String): Result<Unit>{
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun createUser(name: String, email: String, dateBirthday: Timestamp?): Result<Unit>{
        val userId = auth.currentUser?.uid ?: ""
        val usersRef = firestore.collection("users").document(userId)

        val newUser = hashMapOf(
            "id" to userId,
            "name" to name,
            "email" to email,
            "birthday" to dateBirthday
        )

        return try {
            usersRef.set(newUser).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun checkIfUserExists(userId: String?): Boolean{
        return try {
            val userUid = userId ?: auth.currentUser?.uid ?: ""
            val document = firestore.collection("users").document(userUid).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }
}