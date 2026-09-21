package com.example.loginlib.firebase

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

suspend fun getGoogleIdToken(
    context: Context,
    serverClientId: String,
    filterByAuthorizedAccounts: Boolean = false
): Result<String> {
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
        .setServerClientId(serverClientId)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    return try {
        val result = CredentialManager.create(context).getCredential(context, request)
        val credential = GoogleIdTokenCredential.createFrom(result.credential.data)
        Result.success(credential.idToken)
    } catch (e: GetCredentialException) {
        Result.failure(e)
    } catch (e: GoogleIdTokenParsingException) {
        Result.failure(e)
    }
}
