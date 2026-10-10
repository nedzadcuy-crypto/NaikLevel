package com.example.data.sibi

import android.net.Uri

/**
 * Tautan resmi SIBI (Sistem Informasi Perbukuan Indonesia, Kemendikdasmen).
 *
 * CATATAN PENTING (diverifikasi saat audit):
 * - Katalog SIBI adalah aplikasi web berbasis JavaScript. Tidak ditemukan API publik atau
 *   dokumentasi endpoint JSON/PDF yang resmi.
 * - Parameter URL pencarian katalog belum terverifikasi, sehingga aplikasi TIDAK menambahkan
 *   query ke URL katalog. Query pengguna disalin ke clipboard agar bisa ditempel di kolom
 *   pencarian SIBI.
 * - URL detail buku dan URL PDF TIDAK boleh ditebak dari judul/slug.
 */
object SibiLinks {
    const val HOME_URL = "https://buku.kemendikdasmen.go.id/"
    const val CATALOG_URL = "https://buku.kemendikdasmen.go.id/katalog/"

    /** Host yang diizinkan untuk tautan sumber yang ditampilkan di aplikasi. */
    private val ALLOWED_HOSTS = setOf("buku.kemendikdasmen.go.id")

    /** Hanya HTTPS dan hanya host SIBI resmi. */
    fun isAllowedSourceUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        return try {
            val uri = Uri.parse(url.trim())
            uri.scheme.equals("https", ignoreCase = true) &&
                uri.host?.lowercase() in ALLOWED_HOSTS
        } catch (e: Exception) {
            false
        }
    }

    /** Mengembalikan URL jika valid, atau katalog resmi sebagai fallback. */
    fun safeSourceUrlOrCatalog(url: String?): String =
        if (isAllowedSourceUrl(url)) url!!.trim() else CATALOG_URL
}
