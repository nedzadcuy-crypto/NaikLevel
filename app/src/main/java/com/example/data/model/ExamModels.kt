package com.example.data.model

data class ExamQuestion(
    val id: Int,
    val schoolLevel: String,
    val subject: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

object ExamBank {
    val questions: List<ExamQuestion> = listOf(
        // === SD ===
        ExamQuestion(
            id = 1,
            schoolLevel = "SD",
            subject = "Matematika",
            question = "Bentuk paling sederhana dari pecahan 12/16 adalah...",
            options = listOf("2/3", "3/4", "4/5", "6/8"),
            correctAnswerIndex = 1,
            explanation = "FPB dari 12 dan 16 adalah 4. Maka (12 : 4) / (16 : 4) = 3/4."
        ),
        ExamQuestion(
            id = 2,
            schoolLevel = "SD",
            subject = "IPA",
            question = "Tahapan daur hidup kupu-kupu yang sering merugikan petani karena memakan dedaunan adalah...",
            options = listOf("Telur", "Ulat (Larva)", "Kepompong (Pupa)", "Kupu-kupu dewasa"),
            correctAnswerIndex = 1,
            explanation = "Fase ulat (larva) memiliki nafsu makan sangat tinggi untuk mengumpulkan cadangan energi sebelum menjadi kepompong."
        ),
        ExamQuestion(
            id = 3,
            schoolLevel = "SD",
            subject = "Bahasa Indonesia",
            question = "Paragraf yang kalimat utamanya terletak di awal paragraf disebut...",
            options = listOf("Paragraf Induktif", "Paragraf Deduktif", "Paragraf Narasi", "Paragraf Campuran"),
            correctAnswerIndex = 1,
            explanation = "Paragraf deduktif menempatkan gagasan utama di awal, disusul penjelasan rinci."
        ),

        // === SMP ===
        ExamQuestion(
            id = 4,
            schoolLevel = "SMP",
            subject = "Matematika",
            question = "Diketahui segitiga siku-siku memiliki panjang sisi siku-siku 6 cm dan 8 cm. Panjang sisi miringnya adalah...",
            options = listOf("9 cm", "10 cm", "12 cm", "14 cm"),
            correctAnswerIndex = 1,
            explanation = "c = √(6² + 8²) = √(36 + 64) = √100 = 10 cm. Ini kelipatan tripel Pythagoras 3, 4, 5."
        ),
        ExamQuestion(
            id = 5,
            schoolLevel = "SMP",
            subject = "IPA",
            question = "Enzim yang dihasilkan oleh lambung dan berfungsi mengendapkan protein susu (kasein) adalah...",
            options = listOf("Ptialin", "Pepsin", "Renin", "Lipase"),
            correctAnswerIndex = 2,
            explanation = "Enzim renin berfungsi mengendapkan kasein susu pada lambung."
        ),
        ExamQuestion(
            id = 6,
            schoolLevel = "SMP",
            subject = "IPS",
            question = "Tokoh perwakilan Indonesia yang menandatangani Deklarasi Bangkok berdirinya ASEAN tahun 1967 adalah...",
            options = listOf("Adam Malik", "Mohammad Hatta", "Ali Alatas", "Soekarno"),
            correctAnswerIndex = 0,
            explanation = "Menteri Luar Negeri Indonesia yang menandatangani Deklarasi Bangkok adalah Adam Malik."
        ),

        // === SMA ===
        ExamQuestion(
            id = 7,
            schoolLevel = "SMA",
            subject = "Matematika",
            question = "Akar-akar dari persamaan kuadrat x² - 5x + 6 = 0 adalah...",
            options = listOf("x = 1 atau x = 6", "x = -2 atau x = -3", "x = 2 atau x = 3", "x = -1 atau x = -6"),
            correctAnswerIndex = 2,
            explanation = "(x - 2)(x - 3) = 0 sehingga x = 2 atau x = 3."
        ),
        ExamQuestion(
            id = 8,
            schoolLevel = "SMA",
            subject = "IPA",
            question = "Sebuah balok bermassa 5 kg ditarik dengan gaya tetap 20 N pada lantai licin. Percepatan yang dialami balok adalah...",
            options = listOf("2 m/s²", "4 m/s²", "10 m/s²", "100 m/s²"),
            correctAnswerIndex = 1,
            explanation = "Sesuai Hukum II Newton, a = F / m = 20 N / 5 kg = 4 m/s²."
        ),
        ExamQuestion(
            id = 9,
            schoolLevel = "SMA",
            subject = "Bahasa Inggris",
            question = "What is the primary communicative purpose of an Analytical Exposition text?",
            options = listOf(
                "To entertain the readers with a fiction story",
                "To explain how to make or do something step by step",
                "To persuade the reader that an issue is important by presenting arguments",
                "To report the daily breaking news"
            ),
            correctAnswerIndex = 2,
            explanation = "Analytical exposition aims to persuade the audience that the stated viewpoint or issue is valid using logical arguments."
        ),

        // === SMK ===
        ExamQuestion(
            id = 10,
            schoolLevel = "SMK",
            subject = "Informatika & RPL",
            question = "Simbol diagram alir (flowchart) berbentuk belah ketupat (diamond) digunakan untuk melambangkan...",
            options = listOf("Titik awal/akhir program (Terminator)", "Pengambilan keputusan (Decision/Branching)", "Operasi perhitungan (Process)", "Masukan data pengguna (Input)"),
            correctAnswerIndex = 1,
            explanation = "Simbol belah ketupat (diamond) melambangkan pengambilan keputusan dengan kondisi bernilai benar (True) atau salah (False)."
        ),
        ExamQuestion(
            id = 11,
            schoolLevel = "SMK",
            subject = "Teknik Komputer & Jaringan",
            question = "Lapisan pada OSI Layer yang bertanggung jawab untuk menentukan rute logis terbaik (routing) dan menangani IP address adalah...",
            options = listOf("Data Link Layer", "Transport Layer", "Network Layer", "Physical Layer"),
            correctAnswerIndex = 2,
            explanation = "Network Layer (Layer 3) menangani pengalamatan logika (IP Address) dan proses routing paket data."
        ),
        ExamQuestion(
            id = 12,
            schoolLevel = "SMK",
            subject = "Bisnis & Akuntansi",
            question = "Jika perusahaan membeli perlengkapan kantor secara tunai sebesar Rp 500.000, bagaimana pencatatan jurnal umumnya?",
            options = listOf(
                "Kas (D) Rp 500.000, Perlengkapan (K) Rp 500.000",
                "Perlengkapan (D) Rp 500.000, Kas (K) Rp 500.000",
                "Beban Perlengkapan (D) Rp 500.000, Utang Usaha (K) Rp 500.000",
                "Modal (D) Rp 500.000, Kas (K) Rp 500.000"
            ),
            correctAnswerIndex = 1,
            explanation = "Aset perlengkapan bertambah di sisi Debit, dan kas berkurang di sisi Kredit sebesar Rp 500.000."
        )
    )

    fun getQuestionsByLevel(level: String): List<ExamQuestion> {
        val list = questions.filter { it.schoolLevel.equals(level, ignoreCase = true) }
        return if (list.isNotEmpty()) list else questions
    }

    fun getQuestionsByFilter(level: String, subject: String?): List<ExamQuestion> {
        var filtered = questions.filter { it.schoolLevel.equals(level, ignoreCase = true) }
        if (!subject.isNullOrBlank() && subject != "Semua") {
            filtered = filtered.filter { it.subject.contains(subject, ignoreCase = true) }
        }
        return if (filtered.isNotEmpty()) filtered else questions
    }
}
