package com.example.loginlib.validators

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

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

class PhoneNumberMaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length >= 15) text.text.substring(0..14) else text.text
        val maskedText = buildString {
            if (trimmed.isNotEmpty()) {
                append("(")
                if (trimmed.length >= 2) {
                    append(trimmed.take(2))
                }
                append(") ")
                if (trimmed.length >= 5) {
                    append(trimmed.substring(2).take(5))
                }
                append("-")
                // Verifica se trimmed tem pelo menos 8 caracteres
                if (trimmed.length >= 8) {
                    append(trimmed.substring(7))
                }
            }
        }

        val translator = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val transformedLength = maskedText.length
                return when {
                    offset <= 0 -> offset
                    offset <= 2 -> (offset + 1).coerceAtMost(transformedLength)
                    offset <= 7 -> (offset + 3).coerceAtMost(transformedLength)
                    else -> (offset + 4).coerceAtMost(transformedLength)
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val originalLength = text.text.length
                return when {
                    offset <= 0 -> offset
                    offset <= 3 -> (offset - 1).coerceAtMost(originalLength)
                    offset <= 9 -> (offset - 3).coerceAtMost(originalLength)
                    offset <= 14 -> (offset - 4).coerceAtMost(originalLength)
                    else -> originalLength
                }
            }
        }

        return TransformedText(AnnotatedString(maskedText), translator)
    }

}
