package com.example.auth

import com.example.data.auth.AuthValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {
    @Test fun emailValid() = assertNull(AuthValidator.validateEmail("siswa@example.com"))
    @Test fun emailInvalid() = assertNotNull(AuthValidator.validateEmail("siswa@"))
    @Test fun emailBlank() = assertNotNull(AuthValidator.validateEmail("  "))
    @Test fun passwordTooShort() = assertNotNull(AuthValidator.validateNewPassword("abc12"))
    @Test fun passwordNeedsDigit() = assertNotNull(AuthValidator.validateNewPassword("abcdefgh"))
    @Test fun passwordNeedsLetter() = assertNotNull(AuthValidator.validateNewPassword("12345678"))
    @Test fun passwordValid() = assertNull(AuthValidator.validateNewPassword("belajar123"))
    @Test fun confirmMismatch() =
        assertEquals("Konfirmasi password tidak cocok.", AuthValidator.validateConfirm("a1", "a2"))
    @Test fun confirmMatch() = assertNull(AuthValidator.validateConfirm("belajar123", "belajar123"))
    @Test fun nameRequired() = assertNotNull(AuthValidator.validateName(""))
    @Test fun nameValid() = assertNull(AuthValidator.validateName("Rawr"))
    @Test fun loginPasswordRequired() = assertNotNull(AuthValidator.validateLoginPassword(""))
}
