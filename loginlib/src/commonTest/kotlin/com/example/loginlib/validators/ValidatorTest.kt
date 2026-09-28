package com.example.loginlib.validators

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ValidatorTest {

    private val currentYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year

    @Test
    fun validEmails() {
        assertTrue(isValidEmail("user@example.com"))
        assertTrue(isValidEmail("first.last+tag@sub.domain.com.br"))
    }

    @Test
    fun emailsWithSurroundingWhitespaceAreValid() {
        assertTrue(isValidEmail(" user@example.com "))
        assertTrue(isValidEmail("user@example.com\n"))
    }

    @Test
    fun normalizeEmailTrimsWhitespace() {
        assertEquals("user@example.com", normalizeEmail("\t user@example.com  "))
    }

    @Test
    fun invalidEmails() {
        assertFalse(isValidEmail("   "))
        assertFalse(isValidEmail("us er@example.com"))
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("user"))
        assertFalse(isValidEmail("user@"))
        assertFalse(isValidEmail("user@domain"))
        assertFalse(isValidEmail("@domain.com"))
    }

    @Test
    fun passwordNeedsAtLeastSixChars() {
        assertFalse(isValidPassword("12345"))
        assertTrue(isValidPassword("123456"))
    }

    @Test
    fun phoneNumbers() {
        assertTrue(isValidPhoneNumber("(11) 91234-5678"))
        assertTrue(isValidPhoneNumber("(11) 1234-5678"))
        assertFalse(isValidPhoneNumber("11912345678"))
        assertFalse(isValidPhoneNumber("(11) 123-5678"))
    }

    @Test
    fun birthDateOver18() {
        assertTrue(isValidBirthDate("0101${currentYear - 30}"))
        assertTrue(isValidBirthDate("3112${currentYear - 18}"))
    }

    @Test
    fun birthDateUnder18() {
        assertFalse(isValidBirthDate("0101${currentYear - 17}"))
        assertFalse(isValidBirthDate("0101$currentYear"))
    }

    @Test
    fun malformedBirthDate() {
        assertFalse(isValidBirthDate(""))
        assertFalse(isValidBirthDate("0101"))
        assertFalse(isValidBirthDate("ab012000"))
        assertFalse(isValidBirthDate("32132000"))
    }
}
