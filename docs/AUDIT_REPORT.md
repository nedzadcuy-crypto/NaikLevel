# Laporan Audit NaikLevel (statis)

Semua temuan di bawah berasal dari inspeksi kode. Build dan tes TIDAK dijalankan di lingkungan audit
(tidak ada Android SDK, Gradle, atau akses jaringan). Lihat `docs/TEST_REPORT.md`.

## Perbaikan yang diterapkan
| Area | Temuan | Perbaikan |
|---|---|---|
| Build | `debug.keystore` ada di `.gitignore`, tetapi signing debug mewajibkannya | Dipakai hanya jika file ada; jika tidak, signing debug bawaan Android |
| Build | Tidak ada `gradlew`/`gradle-wrapper.jar` di arsip | CI membuat wrapper jika belum ada (file biner tidak dapat dibuat di sini) |
| Build | `BuildConfig.GEMINI_API_KEY` tertanam di APK dan dikirim di URL | Dihapus; AI lewat Firebase AI Logic. Ditambah `GOOGLE_WEB_CLIENT_ID`, `AI_MODEL_NAME` via `.env`/env var |
| AI | Model `gemini-2.5-flash`: dokumentasi Firebase menyatakan model 2.5 dihentikan Oktober 2026 | Default `gemini-3.8-flash`, dapat diganti lewat `AI_MODEL_NAME` (belum diuji nyata) |
| AI | Riwayat chat bisa tidak bergantian user/model; `CancellationException` tertelan; kirim ganda mungkin | Riwayat disanitasi, cancellation diteruskan, guard kirim ganda + `try/finally` |
| AI | Jawaban hardcode berlabel "terverifikasi" | Dihapus; label hanya "materi kamu" / "dokumen SIBI" / "belum diverifikasi SIBI" |
| SIBI | Katalog 22 buku dengan bab, halaman, URL tebakan | Dihapus (arsip `docs/legacy/`); hub ke katalog resmi; tautan divalidasi HTTPS + host |
| SIBI | Pembuat ujian menampilkan "Memuat..." tanpa akhir bila daftar kosong | Pesan jelas + tab awal otomatis ke Catatan Saya |
| Auth | Belum ada | Firebase Auth email/password + Google (Credential Manager), gerbang di `MainActivity` |
| Auth | Chat/ViewModel bertahan setelah logout | ViewModel di-key per `uid`; credential state dibersihkan saat logout |
| Room | `fallbackToDestructiveMigration(dropAllTables = true)` menghapus data pada tiap perubahan versi | Hanya versi 1 yang boleh direset; versi berikutnya wajib Migration. `exportSchema = true` |
| Tes | `ExampleUnitTest` memanggil `generateQuestions(materials, config)` dengan argumen salah urutan (kemungkinan gagal kompilasi) dan menguji katalog palsu | Diperbaiki dengan argumen bernama; tes katalog diganti |

## Belum selesai / risiko
- **Firebase App Check belum diinisialisasi.** Dokumentasi Firebase menyebut enforcement wajib untuk AI Logic mulai 2 Nov 2026. Dependensi ada, kodenya belum.
- Data lokal (Room: materi, XP) tidak dipisah per akun; dua akun di satu perangkat berbagi data.
- Status verifikasi email tidak di-refresh otomatis setelah pengguna memverifikasi (perlu `reload()`).
- String UI masih hardcode (belum dipindah ke `strings.xml`).
- `allowBackup=true` dengan aturan backup bawaan (contoh): belum ditinjau kebijakannya.
- Tidak ada migrasi Room baru, dan tidak ada tes migrasi.
- Tidak ada RAG (di luar lingkup tugas ini).
