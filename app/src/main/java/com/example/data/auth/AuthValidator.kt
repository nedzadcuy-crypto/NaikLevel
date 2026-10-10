package com.example.data.auth

/** Validasi input autentikasi (murni Kotlin, mudah diuji). Mengembalikan pesan error atau null jika valid. */
object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")
    const val MIN_PASSWORD_LENGTH = 8

    fun validateName(name: String): String? {
        val n = name.trim()
        return when {
            n.isEmpty() -> "Nama lengkap wajib diisi."
            n.length < 2 -> "Nama terlalu pendek."
            n.length > 60 -> "Nama maksimal 60 karakter."
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val e = email.trim()
        return when {
            e.isEmpty() -> "Email wajib diisi."
            e.length > 254 || !EMAIL_REGEX.matches(e) -> "Format email tidak valid."
            else -> null
        }
    }

    fun validateLoginPassword(password: String): String? =
        if (password.isEmpty()) "Password wajib diisi." else null

    fun validateNewPassword(password: String): String? = when {
        password.length < MIN_PASSWORD_LENGTH -> "Password minimal $MIN_PASSWORD_LENGTH karakter."
        password.length > 128 -> "Password maksimal 128 karakter."
        password.none { it.isLetter() } || password.none { it.isDigit() } ->
            "Password harus mengandung huruf dan angka."
        else -> null
    }

    fun validateConfirm(password: String, confirm: String): String? =
        if (password != confirm) "Konfirmasi password tidak cocok." else null
}
