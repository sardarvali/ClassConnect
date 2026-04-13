package com.syed.classconnect

import com.syed.classconnect.util.ValidationUtils
import org.junit.Assert.*
import org.junit.Test

class ValidationUtilsTest {
    @Test fun validEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"))
    }
    @Test fun invalidEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("not-an-email"))
    }
    @Test fun blankEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""))
    }
    @Test fun shortPassword_returnsFalse() {
        assertFalse(ValidationUtils.isValidPassword("abc123"))
    }
    @Test fun longEnoughPassword_returnsTrue() {
        assertTrue(ValidationUtils.isValidPassword("abcdefgh"))
    }
    @Test fun passwordsMatch_returnsTrue() {
        assertTrue(ValidationUtils.doPasswordsMatch("password1", "password1"))
    }
    @Test fun passwordsDontMatch_returnsFalse() {
        assertFalse(ValidationUtils.doPasswordsMatch("password1", "password2"))
    }
    @Test fun validClassCode_returnsTrue() {
        assertTrue(ValidationUtils.isValidClassCode("ABC123"))
    }
    @Test fun shortClassCode_returnsFalse() {
        assertFalse(ValidationUtils.isValidClassCode("AB1"))
    }
    @Test fun classCodeWithSpecialChars_returnsFalse() {
        assertFalse(ValidationUtils.isValidClassCode("AB!@12"))
    }
    @Test fun sanitize_trimsWhitespace() {
        assertEquals("hello", ValidationUtils.sanitize("  hello  "))
    }
    @Test fun sanitize_truncatesToMaxLength() {
        val long = "a".repeat(200)
        assertEquals(100, ValidationUtils.sanitize(long, 100).length)
    }
    @Test fun generateCode_hasCorrectLength() {
        val code = ValidationUtils.generateCode(6)
        assertEquals(6, code.length)
        assertTrue(code.all { it.isLetterOrDigit() })
    }
    @Test fun passwordStrength_weakPassword_returnsLow() {
        val strength = ValidationUtils.passwordStrength("abc")
        assertEquals(0, strength)
    }
    @Test fun passwordStrength_strongPassword_returnsHigh() {
        val strength = ValidationUtils.passwordStrength("Abcdef1!")
        assertTrue(strength >= 3)
    }
    @Test fun isNotEmpty_nonBlank_returnsTrue() {
        assertTrue(ValidationUtils.isNotEmpty("hello"))
    }
    @Test fun isNotEmpty_blank_returnsFalse() {
        assertFalse(ValidationUtils.isNotEmpty("   "))
    }
}

class NetworkResultTest {
    @Test fun successResult_holdsData() {
        val result = com.syed.classconnect.util.NetworkResult.Success("test")
        assertEquals("test", result.data)
    }
    @Test fun errorResult_holdsMessage() {
        val result = com.syed.classconnect.util.NetworkResult.Error<String>("error msg", 404)
        assertEquals("error msg", result.message)
        assertEquals(404, result.code)
    }
    @Test fun loadingResult_isLoading() {
        val result = com.syed.classconnect.util.NetworkResult.Loading<String>()
        assertTrue(result is com.syed.classconnect.util.NetworkResult.Loading)
    }
}