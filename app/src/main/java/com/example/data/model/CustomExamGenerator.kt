package com.example.data.model

import com.example.data.local.MaterialEntity

data class CustomExamConfig(
    val examTitle: String,
    val selectedMaterialIds: List<Long> = emptyList(),
    val selectedSibiBookIds: List<String> = emptyList(),
    val questionCount: Int = 5,
    val difficulty: String = "Sedang", // "Mudah", "Sedang", "Sulit"
    val questionType: String = "Semua Tipe", // "Pilihan Ganda", "Benar/Salah", "Semua Tipe"
    val timeLimitMinutes: Int = 5, // 0 means untimed
    val instantFeedback: Boolean = false
)

object CustomExamGenerator {

    sealed class GenerationResult {
        data class Success(
            val questions: List<ExamQuestion>,
            val sourceTitles: List<String>,
            val isOfficialGrounded: Boolean
        ) : GenerationResult()

        data class Error(val message: String) : GenerationResult()
    }

    fun generateQuestions(
        materials: List<MaterialEntity>,
        sibiBooks: List<SibiBook> = emptyList(),
        config: CustomExamConfig
    ): GenerationResult {
        val selectedMaterials = materials.filter { it.id in config.selectedMaterialIds }
        val selectedSibi = sibiBooks.filter { it.id in config.selectedSibiBookIds }

        if (selectedMaterials.isEmpty() && selectedSibi.isEmpty()) {
            return GenerationResult.Error("Silakan pilih minimal 1 materi pribadi atau 1 buku resmi SIBI sebagai sumber bahan ujian.")
        }

        val generated = mutableListOf<ExamQuestion>()
        val sourceTitles = mutableListOf<String>()
        var nextId = 2000

        // 1. Generate questions from selected SIBI Books
        for (book in selectedSibi) {
            sourceTitles.add("${book.title} (SIBI)")
            if (book.chapters.isEmpty()) {
                continue
            }

            for (chapter in book.chapters) {
                val lines = chapter.content.split("\n")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                val headers = lines.filter { it.startsWith("#") }
                    .map { it.replace("#", "").trim() }

                val contentParagraphs = lines.filter { !it.startsWith("#") && it.length > 25 }

                // Question A: Concept comprehension from official SIBI book
                val topicName = headers.firstOrNull() ?: chapter.title
                generated.add(
                    ExamQuestion(
                        id = nextId++,
                        schoolLevel = book.schoolLevel,
                        subject = book.subject,
                        question = "Berdasarkan buku resmi '${book.title}' pada bab '${chapter.title}', fokus pembahasan utama dalam materi '$topicName' adalah...",
                        options = listOf(
                            chapter.summary.ifBlank { "Memahami prinsip dasar dan konsep inti kurikulum resmi." },
                            "Menghafal rumus secara mekanis tanpa memahami penerapan kontekstualnya.",
                            "Mengesampingkan definisi ilmiah demi spekulasi mandiri.",
                            "Menghapus pembuktian ilmiah pada setiap materi pokok."
                        ),
                        correctAnswerIndex = 0,
                        explanation = "Rujukan Resmi: ${book.title} (${chapter.title}, ${chapter.pageRange}) terbitan ${book.publisher}."
                    )
                )

                // Question B: Fact / Statement verification
                if (contentParagraphs.isNotEmpty()) {
                    val samplePara = contentParagraphs.first()
                    val snippet = if (samplePara.length > 95) samplePara.take(90) + "..." else samplePara
                    generated.add(
                        ExamQuestion(
                            id = nextId++,
                            schoolLevel = book.schoolLevel,
                            subject = book.subject,
                            question = "Perhatikan kutipan materi dari buku resmi SIBI '${book.title}' (${chapter.pageRange}):\n\n\"$snippet\"\n\nApakah pernyataan di atas sesuai dengan isi buku resmi?",
                            options = listOf(
                                "Benar, sesuai dengan konsep resmi pada buku SIBI",
                                "Salah, bertentangan dengan prinsip materi kurikulum",
                                "Tidak dapat ditentukan dari isi buku",
                                "Pernyataan tersebut tidak relevan sama sekali"
                            ),
                            correctAnswerIndex = 0,
                            explanation = "Kutipan ini diambil langsung dari ${book.title} (${chapter.title}, ${chapter.pageRange})."
                        )
                    )
                }

                // Question C: Application / Analytical question
                if (contentParagraphs.size > 1) {
                    val secondPara = contentParagraphs[1]
                    val snippet = if (secondPara.length > 85) secondPara.take(80) + "..." else secondPara
                    generated.add(
                        ExamQuestion(
                            id = nextId++,
                            schoolLevel = book.schoolLevel,
                            subject = book.subject,
                            question = "Bagaimana kesimpulan atau aplikasi penting yang dapat ditarik dari uraian: \"$snippet\" pada bab '${chapter.title}'?",
                            options = listOf(
                                "Konsep ini mendasari penyelesaian masalah kontekstual dalam mata pelajaran ${book.subject}.",
                                "Materi ini hanya teoretis dan tidak memiliki aplikasi nyata.",
                                "Konsep ini membatalkan dalil-dalil ilmiah sebelumnya.",
                                "Tidak ada keterkaitan antara konsep ini dengan kehidupan sehari-hari."
                            ),
                            correctAnswerIndex = 0,
                            explanation = "Pembahasan dalam ${book.title} bertujuan memperkuat kompetensi nalar kritis dan aplikasi nyata siswa."
                        )
                    )
                }
            }
        }

        // 2. Generate questions from selected Personal Materials
        for (material in selectedMaterials) {
            sourceTitles.add(material.title)
            val lines = material.content.split("\n")
                .map { it.trim() }
                .filter { it.isNotBlank() }

            val headers = lines.filter { it.startsWith("#") }
                .map { it.replace("#", "").trim() }

            val contentParagraphs = lines.filter { !it.startsWith("#") && it.length > 20 }

            if (headers.isNotEmpty() || material.title.isNotBlank()) {
                val topicName = headers.firstOrNull() ?: material.title
                generated.add(
                    ExamQuestion(
                        id = nextId++,
                        schoolLevel = material.schoolLevel,
                        subject = material.subject,
                        question = "Berdasarkan materi '${material.title}', fokus pembahasan utama dalam topik '$topicName' adalah...",
                        options = listOf(
                            material.description.ifBlank { "Memahami prinsip dasar dan konsep inti yang diajarkan." },
                            "Menghafal istilah tanpa memahami keterkaitannya dengan fenomena nyata.",
                            "Mengesampingkan rumus dan teori dasar demi jalan pintas.",
                            "Menghilangkan tahap pembuktian ilmiah pada setiap materi."
                        ),
                        correctAnswerIndex = 0,
                        explanation = "Sesuai uraian catatan materi '${material.title}', materi difokuskan pada pemahaman menyeluruh tentang $topicName."
                    )
                )
            }

            if (contentParagraphs.isNotEmpty()) {
                val samplePara = contentParagraphs.first()
                val snippet = if (samplePara.length > 95) samplePara.take(90) + "..." else samplePara
                generated.add(
                    ExamQuestion(
                        id = nextId++,
                        schoolLevel = material.schoolLevel,
                        subject = material.subject,
                        question = "Perhatikan pernyataan berikut dari catatan materi '${material.title}':\n\n\"$snippet\"\n\nApakah pernyataan di atas sesuai dan benar menurut isi catatan?",
                        options = listOf(
                            "Benar, sesuai dengan isi catatan materi",
                            "Salah, bertolak belakang dengan konsep materi",
                            "Tidak dapat ditentukan dari isi materi",
                            "Pernyataan tersebut tidak relevan sama sekali"
                        ),
                        correctAnswerIndex = 0,
                        explanation = "Kutipan tersebut diambil langsung dari teks materi '${material.title}'."
                    )
                )
            }
        }

        if (generated.isEmpty()) {
            return GenerationResult.Error("Buku atau materi yang dipilih belum memiliki teks bab yang dapat dianalisis. Silakan pilih buku resmi atau catatan lain.")
        }

        // Filter by question type if requested
        val filteredByType = when (config.questionType) {
            "Benar/Salah" -> generated.filter { it.question.contains("Apakah pernyataan di atas") }
            "Pilihan Ganda" -> generated.filter { !it.question.contains("Apakah pernyataan di atas") }
            else -> generated
        }.ifEmpty { generated }

        val targetCount = config.questionCount.coerceIn(1, 15)
        val finalQuestions = filteredByType.shuffled().take(targetCount)

        return GenerationResult.Success(
            questions = finalQuestions,
            sourceTitles = sourceTitles,
            isOfficialGrounded = selectedSibi.isNotEmpty()
        )
    }
}
