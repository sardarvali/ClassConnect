package com.syed.classconnect

import com.syed.classconnect.util.ValidationUtils
import org.junit.Assert.*
import org.junit.Test

/**
 * AuthRepository unit tests.
 * Tests validation-layer logic used by AuthRepository before any network call.
 * No Android framework or Mockito required — pure Kotlin helpers only.
 */
class AuthRepositoryTest {


    @Test
    fun `register rejects empty name`() {
        assertFalse(ValidationUtils.isNotEmpty(""))
        assertFalse(ValidationUtils.isNotEmpty("   "))
    }

    @Test
    fun `register accepts valid name`() {
        assertTrue(ValidationUtils.isNotEmpty("Alice"))
    }

    @Test
    fun `register rejects invalid email`() {
        assertFalse(ValidationUtils.isValidEmail("not-an-email"))
        assertFalse(ValidationUtils.isValidEmail(""))
    }

    @Test
    fun `register accepts valid email`() {
        assertTrue(ValidationUtils.isValidEmail("alice@school.edu"))
    }

    @Test
    fun `register rejects short password`() {
        assertFalse(ValidationUtils.isValidPassword("1234567"))
    }

    @Test
    fun `register accepts valid password`() {
        assertTrue(ValidationUtils.isValidPassword("Password1!"))
    }

    @Test
    fun `register rejects mismatched passwords`() {
        assertFalse(ValidationUtils.doPasswordsMatch("abc12345", "abc12346"))
    }

    @Test
    fun `register accepts matching passwords`() {
        assertTrue(ValidationUtils.doPasswordsMatch("Pass1234!", "Pass1234!"))
    }

    @Test
    fun `register rejects invalid institution code`() {
        assertFalse(ValidationUtils.isValidClassCode(""))
        assertFalse(ValidationUtils.isValidClassCode("ABC"))        // too short
        assertFalse(ValidationUtils.isValidClassCode("ABC-12"))     // invalid char
    }

    @Test
    fun `register accepts valid institution code`() {
        assertTrue(ValidationUtils.isValidClassCode("ABC123"))
        assertTrue(ValidationUtils.isValidClassCode("abcdef"))
    }

    // -------------------------------------------------------------------
    // Code generation
    // -------------------------------------------------------------------

    @Test
    fun `generateCode creates unique codes`() {
        val codes = (1..100).map { ValidationUtils.generateCode(6) }.toSet()
        // With 36^6 = ~2.2B possibilities, 100 codes should all be unique
        assertTrue(codes.size > 90)
    }

    @Test
    fun `sanitize enforces max length`() {
        val input = "A".repeat(500)
        val result = ValidationUtils.sanitize(input, 100)
        assertEquals(100, result.length)
    }

    @Test
    fun `sanitize trims whitespace`() {
        assertEquals("hello", ValidationUtils.sanitize("  hello  "))
    }
}

