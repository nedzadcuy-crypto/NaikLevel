package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.SibiAiTutorService
import com.example.data.local.AttachmentManager
import com.example.data.local.CurriculumSeed
import com.example.data.local.ExamAttemptEntity
import com.example.data.local.MaterialEntity
import com.example.data.local.NaikLevelDatabase
import com.example.data.local.QuestEntity
import com.example.data.local.SavedAttachment
import com.example.data.local.UserProgressEntity
import com.example.data.local.XpTransactionEntity
import com.example.data.model.CustomExamConfig
import com.example.data.model.CustomExamGenerator
import com.example.data.model.ExamBank
import com.example.data.model.ExamQuestion
import com.example.data.model.SibiBook
import com.example.data.model.SibiCatalog
import com.example.data.sibi.SibiLinks
import com.example.data.model.SibiChapter
import com.example.data.repository.NaikLevelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

sealed class ExtractionStatus {
    object Idle : ExtractionStatus()
    object Extracting : ExtractionStatus()
    data class Success(val message: String) : ExtractionStatus()
    data class Error(val message: String, val preservedAttachment: String?) : ExtractionStatus()
}

data class LearningFilter(
    val schoolLevel: String = "Semua", // "Semua", "SD", "SMP", "SMA", "SMK"
    val grade: Int = 0, // 0 means all grades
    val subject: String = "Semua",
    val curriculum: String = "Semua", // "Semua", "Kurikulum Merdeka"
    val searchQuery: String = "",
    val activeTab: Int = 0 // 0 = Kurikulum Nasional, 1 = Buku Resmi SIBI, 2 = Materi Saya
)

data class ExamSessionState(
    val isActive: Boolean = false,
    val questions: List<ExamQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionId -> selectedOptionIndex
    val remainingSeconds: Int = 300,
    val isUntimed: Boolean = false,
    val isSubmitted: Boolean = false,
    val score: Int = 0,
    val correctCount: Int = 0,
    val earnedXp: Int = 0,
    val subject: String = "",
    val schoolLevel: String = "",
    val sourceMaterialTitle: String? = null,
    val isCustomExam: Boolean = false
)

/** Dasar sebuah jawaban Tutor AI. SIBI_DOCUMENT hanya jika potongan dokumen SIBI benar-benar dipakai. */
enum class AnswerBasis { INFO, USER_MATERIAL, SIBI_DOCUMENT, GENERAL_UNVERIFIED, ERROR }

data class AiTutorMessage(
    val sender: String, // "user" or "ai"
    val text: String,
    val basis: AnswerBasis = AnswerBasis.INFO,
    val sourceTitle: String? = null,
    val sourceUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isSibiVerified: Boolean get() = basis == AnswerBasis.SIBI_DOCUMENT
}

class NaikLevelViewModel(application: Application) : AndroidViewModel(application) {
    private val database = NaikLevelDatabase.getInstance(application)
    private val repository = NaikLevelRepository(
        materialDao = database.materialDao(),
        userProgressDao = database.userProgressDao(),
        questDao = database.questDao(),
        examAttemptDao = database.examAttemptDao(),
        xpTransactionDao = database.xpTransactionDao()
    )

    private val aiTutorService = SibiAiTutorService()

    val userProgress: StateFlow<UserProgressEntity?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val publishedMaterials: StateFlow<List<MaterialEntity>> = repository.publishedMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val draftMaterials: StateFlow<List<MaterialEntity>> = repository.draftMaterials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentMaterial: StateFlow<MaterialEntity?> = repository.recentMaterial
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allQuests: StateFlow<List<QuestEntity>> = repository.allQuests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val examAttempts: StateFlow<List<ExamAttemptEntity>> = repository.examAttempts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Learning Filter State
    private val _learningFilter = MutableStateFlow(LearningFilter())
    val learningFilter: StateFlow<LearningFilter> = _learningFilter.asStateFlow()

    // Filtered materials (Kurikulum Nasional or Materi Saya)
    val filteredMaterials: StateFlow<List<MaterialEntity>> = combine(
        publishedMaterials,
        draftMaterials,
        _learningFilter
    ) { published, drafts, filter ->
        val sourceList = if (filter.activeTab == 0) {
            published.filter { it.source == "KURIKULUM_NASIONAL" }
        } else if (filter.activeTab == 2) {
            // User created section: published user materials + drafts
            (published.filter { it.source == "USER_CREATED" } + drafts)
        } else {
            emptyList()
        }

        sourceList.filter { item ->
            val matchesLevel = filter.schoolLevel == "Semua" || item.schoolLevel.equals(filter.schoolLevel, ignoreCase = true)
            val matchesGrade = filter.grade == 0 || item.grade == filter.grade
            val matchesSubject = filter.subject == "Semua" || item.subject.contains(filter.subject, ignoreCase = true)
            val matchesQuery = filter.searchQuery.isBlank() ||
                    item.title.contains(filter.searchQuery, ignoreCase = true) ||
                    item.description.contains(filter.searchQuery, ignoreCase = true) ||
                    item.topic.contains(filter.searchQuery, ignoreCase = true)
            matchesLevel && matchesGrade && matchesSubject && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // SIBI Official Books Flow with school level, grade, subject, curriculum, and search query filters
    val filteredSibiBooks: StateFlow<List<SibiBook>> = _learningFilter.combine(publishedMaterials) { filter, _ ->
        SibiCatalog.filterBooks(
            level = filter.schoolLevel,
            grade = filter.grade,
            subject = filter.subject,
            curriculum = filter.curriculum,
            query = filter.searchQuery
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SibiCatalog.books)

    // Remembered reading positions for SIBI books (bookId -> last chapter index)
    private val _sibiReadingPositions = MutableStateFlow<Map<String, Int>>(emptyMap())
    val sibiReadingPositions: StateFlow<Map<String, Int>> = _sibiReadingPositions.asStateFlow()

    // Currently opened SIBI book in reader
    private val _activeSibiBook = MutableStateFlow<SibiBook?>(null)
    val activeSibiBook: StateFlow<SibiBook?> = _activeSibiBook.asStateFlow()

    private val _activeSibiChapterIndex = MutableStateFlow(0)
    val activeSibiChapterIndex: StateFlow<Int> = _activeSibiChapterIndex.asStateFlow()

    // Currently opened material for reading
    private val _activeReadingMaterial = MutableStateFlow<MaterialEntity?>(null)
    val activeReadingMaterial: StateFlow<MaterialEntity?> = _activeReadingMaterial.asStateFlow()

    // Attachment in Material Creation
    private val _activeDraftAttachment = MutableStateFlow<SavedAttachment?>(null)
    val activeDraftAttachment: StateFlow<SavedAttachment?> = _activeDraftAttachment.asStateFlow()

    // Material Creation / Import State
    private val _extractionStatus = MutableStateFlow<ExtractionStatus>(ExtractionStatus.Idle)
    val extractionStatus: StateFlow<ExtractionStatus> = _extractionStatus.asStateFlow()

    // Exam Session State
    private val _examSession = MutableStateFlow(ExamSessionState())
    val examSession: StateFlow<ExamSessionState> = _examSession.asStateFlow()

    // AI Tutor Context Source Selection
    private val _tutorContextSource = MutableStateFlow("Semua Sumber (SIBI Kemendikdasmen)")
    val tutorContextSource: StateFlow<String> = _tutorContextSource.asStateFlow()

    // AI Tutor Messages
    private val _tutorMessages = MutableStateFlow<List<AiTutorMessage>>(
        listOf(
            AiTutorMessage(
                sender = "ai",
                text = "Halo! Saya Tutor AI NaikLevel. Tanyakan konsep pelajaran atau minta penjelasan materi. Jawaban hanya diberi label bersumber jika benar-benar memakai materi yang tersedia; selain itu akan ditandai sebagai jawaban AI yang belum diverifikasi. Untuk buku resmi, buka katalog SIBI.",
                basis = AnswerBasis.INFO,
                sourceTitle = "Katalog resmi SIBI",
                sourceUrl = SibiLinks.CATALOG_URL
            )
        )
    )
    val tutorMessages: StateFlow<List<AiTutorMessage>> = _tutorMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> = _isTutorThinking.asStateFlow()

    // User Feedback Toast / SnackBar State
    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> = _userNotice.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            // Seed initial data if database is new
            if (database.materialDao().getTotalMaterialsCountOnce() == 0) {
                database.materialDao().insertMaterials(CurriculumSeed.getInitialMaterials())
                database.questDao().insertQuests(CurriculumSeed.getDefaultQuests())
            }
            if (database.userProgressDao().getUserProgressOnce() == null) {
                database.userProgressDao().insertOrUpdate(
                    UserProgressEntity(
                        id = 1,
                        userName = "Pelajar Indonesia",
                        schoolLevel = "SMA",
                        grade = 10,
                        xp = 0,
                        streakDays = 1,
                        lastActiveDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date()),
                        syncStatus = "LOCAL_ONLY"
                    )
                )
            }
            repository.checkAndUpdateDailyStreak()
        }
    }

    fun clearNotice() {
        _userNotice.value = null
    }

    // Filter controls
    fun setFilterTab(tabIndex: Int) {
        _learningFilter.value = _learningFilter.value.copy(activeTab = tabIndex)
    }

    fun setFilterSchoolLevel(level: String) {
        _learningFilter.value = _learningFilter.value.copy(schoolLevel = level)
    }

    fun setFilterGrade(grade: Int) {
        _learningFilter.value = _learningFilter.value.copy(grade = grade)
    }

    fun setFilterSubject(subject: String) {
        _learningFilter.value = _learningFilter.value.copy(subject = subject)
    }

    fun setFilterCurriculum(curriculum: String) {
        _learningFilter.value = _learningFilter.value.copy(curriculum = curriculum)
    }

    fun setSearchQuery(query: String) {
        _learningFilter.value = _learningFilter.value.copy(searchQuery = query)
    }

    // SIBI Official Books Reader
    fun openSibiBook(book: SibiBook) {
        val lastPos = _sibiReadingPositions.value[book.id] ?: 0
        _activeSibiBook.value = book
        _activeSibiChapterIndex.value = lastPos.coerceIn(0, (book.chapters.size - 1).coerceAtLeast(0))
        _tutorContextSource.value = "Buku SIBI: ${book.title}"
    }

    fun closeSibiBook() {
        _activeSibiBook.value = null
    }

    fun selectSibiChapter(index: Int) {
        val book = _activeSibiBook.value
        _activeSibiChapterIndex.value = index
        if (book != null) {
            _sibiReadingPositions.value = _sibiReadingPositions.value + (book.id to index)
            viewModelScope.launch(Dispatchers.IO) {
                repository.awardXp("read_sibi_${book.id}_ch_$index", 15, "Membaca bab resmi SIBI: ${book.title}")
            }
        }
    }

    // Reading Material
    fun openMaterialForReading(material: MaterialEntity) {
        _activeReadingMaterial.value = material
        _tutorContextSource.value = "Materi: ${material.title}"
    }

    fun closeReading() {
        _activeReadingMaterial.value = null
    }

    fun updateActiveReadingProgress(progress: Float) {
        val current = _activeReadingMaterial.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReadingProgress(current.id, progress)
            val updated = repository.getMaterialById(current.id)
            _activeReadingMaterial.value = updated
        }
    }

    fun markCurrentMaterialFinished() {
        val current = _activeReadingMaterial.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReadingProgress(current.id, 1.0f)
            val updated = repository.getMaterialById(current.id)
            _activeReadingMaterial.value = updated
            _userNotice.value = "Selamat! Materi selesai dipelajari (+40 XP)."
        }
    }

    // Attachment Management in Creation
    fun setDraftAttachment(attachment: SavedAttachment) {
        _activeDraftAttachment.value = attachment
        _extractionStatus.value = ExtractionStatus.Idle
    }

    fun removeDraftAttachment() {
        val current = _activeDraftAttachment.value
        if (current != null) {
            AttachmentManager.deleteAttachment(current.savedPath)
            _activeDraftAttachment.value = null
            _extractionStatus.value = ExtractionStatus.Idle
        }
    }

    fun startExtraction() {
        _extractionStatus.value = ExtractionStatus.Extracting
    }

    fun simulateExtractContent(
        attachment: SavedAttachment,
        onExtracted: (extractedTitle: String, extractedContent: String, extractedTopic: String) -> Unit
    ) {
        viewModelScope.launch {
            _extractionStatus.value = ExtractionStatus.Extracting
            delay(1200)

            val baseName = attachment.displayName.substringBeforeLast(".")
            val formattedTitle = baseName.replace("_", " ").replace("-", " ")
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }

            val topic = if (attachment.mimeType.contains("pdf")) "Dokumen Belajar Digital" else "Catatan Visual"
            val content = when (attachment.type) {
                "IMAGE" -> """
                    # $formattedTitle
                    
                    ## Ringkasan Catatan Visual
                    Materi ini diekstrak dari gambar/foto dokumen (${attachment.displayName}, ${attachment.sizeBytes / 1024} KB).
                    
                    ### Poin-Poin Utama
                    - Telah tersimpan aman dalam memori internal aplikasi NaikLevel.
                    - Lampiran gambar dapat dilihat langsung saat membuka materi ini.
                    - Kamu dapat menambahkan catatan penjelasan lebih mendalam di bawah ini.
                """.trimIndent()
                else -> """
                    # $formattedTitle
                    
                    ## Intisari Berkas Dokumen
                    Dokumen lampiran (${attachment.displayName}, tipe ${attachment.mimeType}) berhasil ditautkan.
                    
                    ### Pembahasan Materi
                    - Berkas dokumen telah dienkapsulasi pada folder aplikasi: attachments/${attachment.displayName}.
                    - Gunakan editor ini untuk menuliskan ringkasan atau pertanyaan seputar dokumen tersebut.
                """.trimIndent()
            }

            _extractionStatus.value = ExtractionStatus.Success("Konten berhasil diolah dari lampiran.")
            onExtracted(formattedTitle, content, topic)
        }
    }

    fun clearExtractionStatus() {
        _extractionStatus.value = ExtractionStatus.Idle
    }

    // Material Persistence
    fun saveCreatedMaterial(
        title: String,
        description: String,
        content: String,
        schoolLevel: String,
        grade: Int,
        subject: String,
        topic: String,
        isDraft: Boolean,
        onSuccess: () -> Unit
    ) {
        if (title.isBlank()) {
            _userNotice.value = "Judul materi tidak boleh kosong."
            return
        }

        val draftAtt = _activeDraftAttachment.value
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMaterial(
                title = title.trim(),
                description = description.trim(),
                content = content.trim(),
                schoolLevel = schoolLevel,
                grade = grade,
                subject = subject,
                topic = topic.trim(),
                isDraft = isDraft,
                attachmentName = draftAtt?.displayName,
                attachmentNote = draftAtt?.let { "${it.type} (${it.sizeBytes / 1024} KB)" },
                attachmentPath = draftAtt?.savedPath,
                attachmentType = draftAtt?.type
            )
            _activeDraftAttachment.value = null
            _extractionStatus.value = ExtractionStatus.Idle
            _userNotice.value = if (isDraft) "Draft materi berhasil disimpan." else "Materi baru berhasil diterbitkan!"
            viewModelScope.launch(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    fun deleteMaterial(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMaterial(id)
            _userNotice.value = "Materi telah dihapus."
        }
    }

    // Quests
    fun claimQuest(quest: QuestEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.claimQuest(quest.id)
            if (success) {
                _userNotice.value = "Berhasil mengklaim +${quest.xpReward} XP untuk quest: ${quest.title}!"
            } else {
                _userNotice.value = "Quest belum selesai atau sudah pernah diklaim sebelumnya."
            }
        }
    }

    // Predefined Exam Simulation
    fun startExamSimulation(schoolLevel: String, subject: String = "Semua") {
        val questions = ExamBank.getQuestionsByFilter(schoolLevel, subject).shuffled().take(5)
        _examSession.value = ExamSessionState(
            isActive = true,
            questions = questions,
            currentQuestionIndex = 0,
            userAnswers = emptyMap(),
            remainingSeconds = 300,
            isUntimed = false,
            isSubmitted = false,
            score = 0,
            correctCount = 0,
            earnedXp = 0,
            subject = if (subject == "Semua") "Simulasi Ujian Terpadu" else subject,
            schoolLevel = schoolLevel,
            sourceMaterialTitle = "Bank Soal Kurikulum Nasional",
            isCustomExam = false
        )
    }

    // Custom Exam Generation from User's Materials & SIBI Official Books
    fun startCustomExam(config: CustomExamConfig) {
        val allMaterials = publishedMaterials.value + draftMaterials.value
        val result = CustomExamGenerator.generateQuestions(
            materials = allMaterials,
            sibiBooks = SibiCatalog.books,
            config = config
        )
        when (result) {
            is CustomExamGenerator.GenerationResult.Error -> {
                _userNotice.value = result.message
            }
            is CustomExamGenerator.GenerationResult.Success -> {
                val totalSeconds = if (config.timeLimitMinutes > 0) config.timeLimitMinutes * 60 else 0
                _examSession.value = ExamSessionState(
                    isActive = true,
                    questions = result.questions,
                    currentQuestionIndex = 0,
                    userAnswers = emptyMap(),
                    remainingSeconds = totalSeconds,
                    isUntimed = config.timeLimitMinutes == 0,
                    isSubmitted = false,
                    score = 0,
                    correctCount = 0,
                    earnedXp = 0,
                    subject = config.examTitle,
                    schoolLevel = result.questions.firstOrNull()?.schoolLevel ?: "SMA",
                    sourceMaterialTitle = result.sourceTitles.joinToString(", "),
                    isCustomExam = true
                )
                _userNotice.value = "Ujian '${config.examTitle}' siap dikerjakan (${result.questions.size} soal)."
            }
        }
    }

    fun selectExamAnswer(questionId: Int, optionIndex: Int) {
        val current = _examSession.value
        if (!current.isActive || current.isSubmitted) return
        val updatedAnswers = current.userAnswers.toMutableMap()
        updatedAnswers[questionId] = optionIndex
        _examSession.value = current.copy(userAnswers = updatedAnswers)
    }

    fun nextExamQuestion() {
        val current = _examSession.value
        if (current.currentQuestionIndex < current.questions.size - 1) {
            _examSession.value = current.copy(currentQuestionIndex = current.currentQuestionIndex + 1)
        }
    }

    fun previousExamQuestion() {
        val current = _examSession.value
        if (current.currentQuestionIndex > 0) {
            _examSession.value = current.copy(currentQuestionIndex = current.currentQuestionIndex - 1)
        }
    }

    fun submitExamSimulation() {
        val current = _examSession.value
        if (!current.isActive || current.isSubmitted) return

        var correct = 0
        for (q in current.questions) {
            val selected = current.userAnswers[q.id]
            if (selected != null && selected == q.correctAnswerIndex) {
                correct++
            }
        }
        val total = current.questions.size
        val score = if (total > 0) (correct * 100) / total else 0
        val durationTaken = if (current.isUntimed) 60 else (300 - current.remainingSeconds).coerceAtLeast(10)

        // Build snapshot of user answers and explanations to preserve historical results
        val answersSnapshot = JSONArray().apply {
            for (q in current.questions) {
                val userChoice = current.userAnswers[q.id]
                put(JSONObject().apply {
                    put("questionId", q.id)
                    put("question", q.question)
                    put("subject", q.subject)
                    put("userAnswerIndex", userChoice ?: -1)
                    put("correctAnswerIndex", q.correctAnswerIndex)
                    put("userAnswerText", q.options.getOrNull(userChoice ?: -1) ?: "Belum dijawab")
                    put("correctAnswerText", q.options.getOrNull(q.correctAnswerIndex) ?: "")
                    put("isCorrect", userChoice == q.correctAnswerIndex)
                    put("explanation", q.explanation)
                })
            }
        }
        val reviewSummaryJson = answersSnapshot.toString()

        viewModelScope.launch(Dispatchers.IO) {
            val attemptId = repository.recordExamAttempt(
                subject = current.subject,
                schoolLevel = current.schoolLevel,
                totalQuestions = total,
                correctAnswers = correct,
                durationSeconds = durationTaken,
                sourceMaterialTitle = current.sourceMaterialTitle,
                isCustomExam = current.isCustomExam,
                answersJson = reviewSummaryJson,
                reviewSummaryJson = reviewSummaryJson
            )
            val xpGained = 30 + (score / 2)
            _examSession.value = current.copy(
                isSubmitted = true,
                score = score,
                correctCount = correct,
                earnedXp = xpGained
            )
            _userNotice.value = "Ujian selesai! Skor: $score/100 (+${xpGained} XP)"
        }
    }

    fun closeExamSession() {
        _examSession.value = ExamSessionState()
    }

    // AI Tutor with SIBI & Material RAG Retrieval
    fun setTutorContextSource(source: String) {
        _tutorContextSource.value = source
    }

    fun sendAiTutorQuestion(query: String, explicitSource: String? = null) {
        if (query.isBlank() || query.length > 1000 || _isTutorThinking.value) return

        val currentList = _tutorMessages.value.toMutableList()
        currentList.add(AiTutorMessage(sender = "user", text = query.trim()))
        _tutorMessages.value = currentList

        _isTutorThinking.value = true
        viewModelScope.launch {
          try {
            repository.onAiTutorQueried()

            val aiAnswer = aiTutorService.answerQuestion(
                query = query.trim(),
                gradeLevel = userProgress.value?.let { "${it.schoolLevel} kelas ${it.grade}" } ?: "tidak diketahui",
                history = currentList.dropLast(1),
                activeSibiBook = _activeSibiBook.value,
                activeChapterIndex = _activeSibiChapterIndex.value,
                activeReadingMaterial = _activeReadingMaterial.value,
                sibiCatalog = SibiCatalog.books,
                savedMaterials = publishedMaterials.value + draftMaterials.value
            )

            val updatedList = _tutorMessages.value.toMutableList()
            updatedList.add(aiAnswer)
            _tutorMessages.value = updatedList
          } finally {
            _isTutorThinking.value = false
          }
        }
    }

    // Profile & Sync controls
    fun updateProfile(name: String, schoolLevel: String, grade: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(name, schoolLevel, grade)
            _userNotice.value = "Profil berhasil diperbarui."
        }
    }

    fun triggerSyncCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSyncStatus("PENDING")
            delay(1200)
            repository.updateSyncStatus("LOCAL_ONLY")
            _userNotice.value = "Status sinkronisasi: Data tersimpan aman secara offline pada penyimpanan lokal."
        }
    }

    fun resetData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetLearningData()
            _userNotice.value = "Data riwayat ujian dan progres belajar telah direset."
        }
    }
}
