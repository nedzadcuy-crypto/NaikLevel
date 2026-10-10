package com.example.data.model

/**
 * Model buku SIBI.
 *
 * Katalog hardcode lama (22 buku dengan bab, rentang halaman, dan tautan hasil tebakan) telah
 * dihapus karena tidak dapat diverifikasi terhadap SIBI dan sebelumnya ditampilkan seolah-olah
 * data resmi. Salinan arsipnya ada di docs/legacy/SibiBook.kt.txt.
 *
 * Tipe data dipertahankan agar layar lama tetap dapat dikompilasi. Sumber data buku yang sah
 * (impor dokumen yang diizinkan, atau API resmi jika kelak dipublikasikan) dapat mengisi
 * [SibiCatalog.books] tanpa mengubah UI.
 */
data class SibiChapter(
    val chapterNumber: Int,
    val title: String,
    val summary: String,
    val content: String,
    val pageRange: String
)

data class SibiBook(
    val id: String,
    val title: String,
    val schoolLevel: String,
    val grade: Int,
    val subject: String,
    val curriculum: String = "",
    val bookType: String = "",
    val authors: String = "",
    val publisher: String = "",
    val year: Int = 0,
    val coverColorHex: Long = 0xFF2563EB,
    val synopsis: String = "",
    val officialUrl: String,
    val pdfDownloadUrl: String = "",
    val chapters: List<SibiChapter> = emptyList()
)

object SibiCatalog {
    /** Kosong sampai ada sumber data buku yang sah dan terverifikasi. */
    val books: List<SibiBook> = emptyList()

    fun filterBooks(
        level: String = "Semua",
        grade: Int = 0,
        subject: String = "Semua",
        curriculum: String = "Semua",
        query: String = ""
    ): List<SibiBook> = books.filter { book ->
        (level == "Semua" || book.schoolLevel.equals(level, ignoreCase = true)) &&
            (grade == 0 || book.grade == grade) &&
            (subject == "Semua" || book.subject.contains(subject, ignoreCase = true)) &&
            (query.isBlank() || book.title.contains(query, ignoreCase = true))
    }

    fun getBookById(id: String): SibiBook? = books.find { it.id == id }

    fun getDistinctSubjects(): List<String> =
        listOf("Semua", "Matematika", "IPA", "Bahasa Indonesia", "Informatika", "Kejuruan SMK")

    fun getSubjectBookCount(subject: String): Int =
        if (subject == "Semua") books.size else books.count { it.subject.contains(subject, ignoreCase = true) }

    fun getCatalogNotice(subject: String): String? = null
}
