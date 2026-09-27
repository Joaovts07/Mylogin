package com.example.loginlib.validators

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

// Same pattern as android.util.Patterns.EMAIL_ADDRESS
private val EMAIL_ADDRESS = Regex(
    "[a-zA-Z0-9+._%\\-]{1,256}" +
        "@" +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
        "(" +
        "\\." +
        "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
        ")+"
)

fun isValidEmail(email: String): Boolean {
    return EMAIL_ADDRESS.matches(email)
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

// birthDate is in ddMMyyyy format
fun isValidBirthDate(birthDate: String): Boolean {
    if (birthDate.length != 8 || !birthDate.all { it.isDigit() }) return false
    val date = runCatching {
        LocalDate(
            year = birthDate.substring(4, 8).toInt(),
            monthNumber = birthDate.substring(2, 4).toInt(),
            dayOfMonth = birthDate.substring(0, 2).toInt()
        )
    }.getOrNull() ?: return false
    val currentYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year
    val age = currentYear - date.year
    return age >= 18
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Regex("""^\(\d{2}\)\s\d{4,5}-\d{4}$""") // (XX) XXXX-XXXX ou (XX) XXXXX-XXXX
    return pattern.matches(phoneNumber)
}
