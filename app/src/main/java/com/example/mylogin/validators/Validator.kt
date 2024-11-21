package com.example.mylogin.validators

import android.util.Patterns
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.SimpleDateFormat
import java.util.*

// Funções de validação
fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}

fun isValidBirthDate(birthDate: String): Boolean {
    return try {
        val date = SimpleDateFormat("ddMMyyyy", Locale.getDefault()).parse(birthDate)
        val birthYear = Calendar.getInstance().apply { time = date }.get(Calendar.YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = currentYear - birthYear
        age >= 18
    } catch (e: Exception) {
        false
    }
}

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Regex("""^\(\d{2}\)\s\d{4,5}-\d{4}$""") // (XX) XXXX-XXXX ou (XX) XXXXX-XXXX
    return pattern.matches(phoneNumber)
}

class DateMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 10) text.text.substring(0..9) else text.text
        val maskedText = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i % 2 == 1 && i < 4) append("/")
            }
        }

        val numberOffsetTranslator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                var transformedOffset = offset
                for (i in 0 until offset) {
                    if (i % 2 == 1 && i < 4) transformedOffset++
                }
                return transformedOffset.coerceAtMost(maskedText.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                var originalOffset = offset
                var i = 1
                while (i < offset && i < maskedText.length) {
                    if (maskedText[i] == '/') originalOffset--
                    i++
                }
                return originalOffset.coerceAtMost(trimmed.length)
            }
        }
        return TransformedText(AnnotatedString(maskedText), numberOffsetTranslator)
    }
}