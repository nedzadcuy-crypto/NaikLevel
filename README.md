# NaikLevel
> **Belajar Modern**
> Created by Nedzad Elvedino Ariesza

---

## Tentang Aplikasi
**NaikLevel** adalah aplikasi edukasi native Android dengan antarmuka modern, bersih, dan intuitif. Dirancang untuk membantu siswa di seluruh Indonesia dari jenjang **SD, SMP, SMA, hingga SMK** belajar secara mandiri, interaktif, dan terstruktur.

Aplikasi ini menggabungkan kurikulum standar nasional, sistem pembuatan materi mandiri (teks, dokumen, dan pindai foto), gamifikasi berbasis XP, streak belajar harian, simulasi ujian berwaktu, dan bimbingan interaktif bersama Tutor AI NaikLevel.

---

## Fitur Utama

1. **Kurikulum Lengkap (SD, SMP, SMA, SMK)**
   - Tersedia materi kurikulum terstruktur untuk Sekolah Dasar, Menengah Pertama, Menengah Atas, dan Kejuruan (SMK).
   - Filter berdasarkan jenjang, kelas (1–12), mata pelajaran (Matematika, IPA, IPS, Bahasa Indonesia, Bahasa Inggris, Informatika/RPL, dll.), dan topik pembahasan.

2. **Pembuatan Materi Fleksibel & Ekstraksi Teks**
   - Tambah materi belajar dari catatan manual, dokumen (.txt), maupun foto/kamera.
   - Status ekstraksi transparan (*Idle, Extracting, Success, Error*) dengan perlindungan berkas lampiran asli.
   - Simpan draf atau langsung terbitkan ke perpustakaan belajar pribadi.

3. **Gamifikasi & Misi Belajar (Idempotent XP Rewards)**
   - Progres level dinamis (*Level 1 – Pelajar Pemula* hingga *Master Edukasi*).
   - Sistem **XP Idempoten**: Penekanan berulang tidak akan menggandakan hadiah XP.
   - Streak belajar harian untuk membangun kebiasaan belajar konsisten.
   - Quest Harian, Quest Mingguan, dan Lencana Prestasi.

4. **Simulasi Ujian Berwaktu (Exam Simulation)**
   - Soal latihan pilihan ganda sesuai jenjang sekolah.
   - Timer countdown, perekaman jawaban, perhitungan skor otomatis (0–100), dan pembahasan jawaban mendalam.

5. **Statistik Belajar Komprehensif**
   - Total materi yang diselesaikan, riwayat ujian, dan akurasi per mata pelajaran.
   - Penjelasan informatif ketika data belum mencukupi untuk evaluasi andal.

6. **Tutor AI NaikLevel**
   - Menjawab lewat Firebase AI Logic (tanpa API key di APK). Jawaban diberi label dasar yang jujur: "Berdasarkan materi kamu", "Berdasarkan dokumen SIBI" (hanya jika potongan dokumen SIBI benar-benar dipakai), atau "Belum diverifikasi SIBI". Jika layanan AI gagal, pesan error ditampilkan (tidak ada jawaban hardcode).

6b. **Perpustakaan SIBI**
   - SIBI tidak menyediakan API publik terdokumentasi (katalog adalah aplikasi web JavaScript), sehingga NaikLevel tidak menampilkan daftar buku tiruan. Layar ini mengarahkan ke katalog resmi https://buku.kemendikdasmen.go.id/katalog/ dan menyalin kata kunci pencarian ke clipboard. Katalog hardcode lama diarsipkan di `docs/legacy/`.

6c. **Akun (Login & Sign Up)**
   - Firebase Authentication: email/password dan Google Sign-In (Credential Manager). Lihat bagian Konfigurasi.

7. **Offline-First & Penyimpanan Andal**
   - Menggunakan **Room Database** lokal sehingga materi dapat diakses tanpa kuota internet.
   - Representasi status sinkronisasi yang akurat dan aman.

8. **Tentang & Atribusi Pengembang**
   - Layar Tentang Aplikasi menampilkan identitas lengkap:
     - **NaikLevel**
     - **Belajar Modern**
     - **Created by Nedzad Elvedino Ariesza**

---

## Arsitektur & Teknologi
- **Bahasa**: Kotlin (100%)
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Arsitektur**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Penyimpanan Lokal**: Room Database + StateFlow & Coroutines
- **Desain & Warna**:
  - Primary Color: `#2563EB` (Royal Blue)
  - Secondary Surface: `#DBEAFE` (Soft Sky Blue)
  - Primary Text: `#0F172A` (Slate Dark)
  - Secondary Text: `#64748B` (Slate Muted)
  - Success: `#16A34A` (Emerald Green)

---

## Konfigurasi Firebase (wajib untuk login dan Tutor AI)
1. Buat proyek Firebase, daftarkan app Android dengan `applicationId` di `app/build.gradle.kts`, lalu taruh `google-services.json` di folder `app/`.
2. Firebase Console > Authentication > Sign-in method: aktifkan **Email/Password** dan **Google**. Tambahkan SHA-1/SHA-256 keystore debug/release di pengaturan app Android.
3. (Opsional) isi `GOOGLE_WEB_CLIENT_ID` di `.env` (lihat `.env.example`) jika `google-services.json` tidak memuat `default_web_client_id`.
4. Firebase Console > AI Logic: aktifkan Gemini Developer API. Model default `gemini-3.8-flash` dapat diganti lewat `AI_MODEL_NAME` di `.env`.
5. Disarankan mengaktifkan Firebase App Check agar layanan AI tidak disalahgunakan.

## Setup lokal dan pengujian
```bash
cp .env.example .env            # isi GOOGLE_WEB_CLIENT_ID jika perlu (opsional)
gradle wrapper --gradle-version 9.3.1   # hanya jika gradlew belum ada
./gradlew assembleDebug
./gradlew testDebugUnitTest
```
CI: `.github/workflows/android-ci.yml`. Laporan: `docs/AUDIT_REPORT.md`, `docs/TEST_REPORT.md`.
Catatan: Firebase App Check perlu disiapkan sebelum 2 Nov 2026 agar Tutor AI tetap berfungsi.

## Cara Membangun APK (Build Instructions)

Untuk membuat file APK di lingkungan lokal atau server:

### 1. Prasyarat
- Android SDK (API Level 36 / 35, Min SDK 24)
- JDK 17 atau JDK 21
- Gradle

### 2. Perintah Build APK Debug
Jalankan perintah berikut pada terminal di direktori utama:
```bash
gradle assembleDebug
```
File APK yang berhasil dibangun akan berada di:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Perintah Build APK Release
```bash
gradle assembleRelease
```
File APK release akan berada di:
`app/build/outputs/apk/release/app-release-unsigned.apk`

---

## GitHub Actions Workflow
Alur kerja integrasi berkelanjutan (CI) otomatis dikonfigurasi dalam `.github/workflows/android.yml` untuk memvalidasi kompilasi dan menghasilkan artifact APK pada setiap push/pull request.

---

## Atribusi & Hak Cipta
**Created by Nedzad Elvedino Ariesza**  
Hak Cipta © 2026 NaikLevel • Belajar Modern.
