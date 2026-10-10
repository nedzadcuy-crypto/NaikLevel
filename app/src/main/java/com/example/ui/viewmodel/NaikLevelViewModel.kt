
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
import com.example.data.model.CustomExamConfig
import com.example.data.model.CustomExamGenerator
import com.example.data.model.ExamBank
import com.example.data.model.ExamQuestion
import com.example.data.model.SibiBook
import com.example.data.model.SibiCatalog
import com.example.data.sibi.SibiLinks
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

sealed class ExtractionStatus {
    object Idle : ExtractionStatus()
    object Extracting : ExtractionStatus()
    data class Success(val message: String) : ExtractionStatus()
    data class Error(
        val message: String,
        val preservedAttachment: String?
    ) : ExtractionStatus()
}

data class LearningFilter(
    val schoolLevel: String = "Semua",
    val grade: Int = 0,
    val subject: String = "Semua",
    val curriculum: String = "Semua",
    val searchQuery: String = "",
    val activeTab: Int = 0
)

data class ExamSessionState(
    val isActive: Boolean = false,
    val questions: List<ExamQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(),
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

enum class AnswerBasis {
    INFO,
    USER_MATERIAL,
    SIBI_DOCUMENT,
    GENERAL_UNVERIFIED,
    ERROR
}

data class AiTutorMessage(
    val sender: String,
    val text: String,
    val basis: AnswerBasis = AnswerBasis.INFO,
    val sourceTitle: String? = null,
    val sourceUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isSibiVerified: Boolean
        get() = basis == AnswerBasis.SIBI_DOCUMENT
}

class NaikLevelViewModel(application: Application) :
    AndroidViewModel(application) {

    private val database = NaikLevelDatabase.getInstance(application)

    private val repository = NaikLevelRepository(
        materialDao = database.materialDao(),
        userProgressDao = database.userProgressDao(),
        questDao = database.questDao(),
        examAttemptDao = database.examAttemptDao(),
        xpTransactionDao = database.xpTransactionDao()
    )

    private val aiTutorService = SibiAiTutorService()

    val userProgress: StateFlow<UserProgressEntity?> =
        repository.userProgress.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val publishedMaterials: StateFlow<List<MaterialEntity>> =
        repository.publishedMaterials.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val draftMaterials: StateFlow<List<MaterialEntity>> =
        repository.draftMaterials.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val recentMaterial: StateFlow<MaterialEntity?> =
        repository.recentMaterial.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val allQuests: StateFlow<List<QuestEntity>> =
        repository.allQuests.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val examAttempts: StateFlow<List<ExamAttemptEntity>> =
        repository.examAttempts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private val _learningFilter = MutableStateFlow(LearningFilter())
    val learningFilter: StateFlow<LearningFilter> =
        _learningFilter.asStateFlow()

    val filteredMaterials: StateFlow<List<MaterialEntity>> =
        combine(
            publishedMaterials,
            draftMaterials,
            _learningFilter
        ) { published, drafts, filter ->
            val sourceList = when (filter.activeTab) {
                0 -> published.filter {
                    it.source == "KURIKULUM_NASIONAL"
                }
                2 -> published.filter {
                    it.source == "USER_CREATED"
                } + drafts
                else -> emptyList()
            }

            sourceList.filter { item ->
                val matchesLevel =
                    filter.schoolLevel == "Semua" ||
                        item.schoolLevel.equals(
                            filter.schoolLevel,
                            ignoreCase = true
                        )

                val matchesGrade =
                    filter.grade == 0 || item.grade == filter.grade

                val matchesSubject =
                    filter.subject == "Semua" ||
                        item.subject.contains(
                            filter.subject,
                            ignoreCase = true
                        )

                val matchesQuery =
                    filter.searchQuery.isBlank() ||
                        item.title.contains(
                            filter.searchQuery,
                            ignoreCase = true
                        ) ||
                        item.description.contains(
                            filter.searchQuery,
                            ignoreCase = true
                        ) ||
                        item.topic.contains(
                            filter.searchQuery,
                            ignoreCase = true
                        )

                matchesLevel &&
                    matchesGrade &&
                    matchesSubject &&
                    matchesQuery
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val filteredSibiBooks: StateFlow<List<SibiBook>> =
        _learningFilter.combine(publishedMaterials) { filter, _ ->
            SibiCatalog.filterBooks(
                level = filter.schoolLevel,
                grade = filter.grade,
                subject = filter.subject,
                curriculum = filter.curriculum,
                query = filter.searchQuery
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            SibiCatalog.books
        )

    private val _sibiReadingPositions =
        MutableStateFlow<Map<String, Int>>(emptyMap())

    val sibiReadingPositions: StateFlow<Map<String, Int>> =
        _sibiReadingPositions.asStateFlow()

    private val _activeSibiBook =
        MutableStateFlow<SibiBook?>(null)

    val activeSibiBook: StateFlow<SibiBook?> =
        _activeSibiBook.asStateFlow()

    private val _activeSibiChapterIndex = MutableStateFlow(0)
    val activeSibiChapterIndex: StateFlow<Int> =
        _activeSibiChapterIndex.asStateFlow()

    private val _activeReadingMaterial =
        MutableStateFlow<MaterialEntity?>(null)

    val activeReadingMaterial: StateFlow<MaterialEntity?> =
        _activeReadingMaterial.asStateFlow()

    private val _activeDraftAttachment =
        MutableStateFlow<SavedAttachment?>(null)

    val activeDraftAttachment: StateFlow<SavedAttachment?> =
        _activeDraftAttachment.asStateFlow()

    private val _extractionStatus =
        MutableStateFlow<ExtractionStatus>(ExtractionStatus.Idle)

    val extractionStatus: StateFlow<ExtractionStatus> =
        _extractionStatus.asStateFlow()

    private val _examSession = MutableStateFlow(ExamSessionState())
    val examSession: StateFlow<ExamSessionState> =
        _examSession.asStateFlow()

    private val _tutorContextSource =
        MutableStateFlow("Semua Sumber (SIBI Kemendikdasmen)")

    val tutorContextSource: StateFlow<String> =
        _tutorContextSource.asStateFlow()

    private val _tutorMessages = MutableStateFlow(
        listOf(
            AiTutorMessage(
                sender = "ai",
                text = "halo! saya tutor ai naiklevel. tanyakan konsep pelajaran atau minta penjelasan materi. jawaban hanya diberi label bersumber jika benar-benar memakai materi yang tersedia.",
                basis = AnswerBasis.INFO,
                sourceTitle = "Katalog resmi SIBI",
                sourceUrl = SibiLinks.CATALOG_URL
            )
        )
    )

    val tutorMessages: StateFlow<List<AiTutorMessage>> =
        _tutorMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> =
        _isTutorThinking.asStateFlow()

    private val _userNotice = MutableStateFlow<String?>(null)
    val userNotice: StateFlow<String?> =
        _userNotice.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (database.materialDao().getTotalMaterialsCountOnce() == 0) {
                database.materialDao().insertMaterials(
                    CurriculumSeed.getInitialMaterials()
                )
                database.questDao().insertQuests(
                    CurriculumSeed.getDefaultQuests()
                )
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
                        lastActiveDate = java.text.SimpleDateFormat(
                            "yyyy-MM-dd",
                            java.util.Locale.US
                        ).format(java.util.Date()),
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

    fun setFilterTab(tabIndex: Int) {
        _learningFilter.value =
            _learningFilter.value.copy(activeTab = tabIndex)
    }

    fun setFilterSchoolLevel(level: String) {
        _learningFilter.value =
            _learningFilter.value.copy(schoolLevel = level)
    }

    fun setFilterGrade(grade: Int) {
        _learningFilter.value =
            _learningFilter.value.copy(grade = grade)
    }

    fun setFilterSubject(subject: String) {
        _learningFilter.value =
            _learningFilter.value.copy(subject = subject)
    }

    fun setFilterCurriculum(curriculum: String) {
        _learningFilter.value =
            _learningFilter.value.copy(curriculum = curriculum)
    }

    fun setSearchQuery(query: String) {
        _learningFilter.value =
            _learningFilter.value.copy(searchQuery = query)
    }

    fun openSibiBook(book: SibiBook) {
        val lastPos = _sibiReadingPositions.value[book.id] ?: 0
        _activeSibiBook.value = book
        _activeSibiChapterIndex.value = lastPos.coerceIn(
            0,
            (book.chapters.size - 1).coerceAtLeast(0)
        )
        _tutorContextSource.value = "Buku SIBI: ${book.title}"
    }

    fun closeSibiBook() {
        _activeSibiBook.value = null
    }

    fun selectSibiChapter(index: Int) {
        val book = _activeSibiBook.value ?: return
        if (index !in book.chapters.indices) return

        _activeSibiChapterIndex.value = index
        _sibiReadingPositions.value =
            _sibiReadingPositions.value + (book.id to index)

        viewModelScope.launch(Dispatchers.IO) {
            repository.awardXp(
                "read_sibi_${book.id}_ch_$index",
                15,
                "Membaca bab resmi SIBI: ${book.title}"
            )
        }
    }

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
            _activeReadingMaterial.value =
                repository.getMaterialById(current.id)
        }
    }

    fun markCurrentMaterialFinished() {
        val current = _activeReadingMaterial.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateReadingProgress(current.id, 1f)
            _activeReadingMaterial.value =
                repository.getMaterialById(current.id)

            _userNotice.value =
                "Selamat! Materi selesai dipelajari (+40 XP)."
        }
    }

    fun setDraftAttachment(attachment: SavedAttachment) {
        _activeDraftAttachment.value = attachment
        _extractionStatus.value = ExtractionStatus.Idle
    }

    fun removeDraftAttachment() {
        _activeDraftAttachment.value?.let {
            AttachmentManager.deleteAttachment(it.localPath)
        }
        _activeDraftAttachment.value = null
        _extractionStatus.value = ExtractionStatus.Idle
    }

    fun startExtraction() {
        val attachment = _activeDraftAttachment.value
        if (attachment == null) {
            _extractionStatus.value = ExtractionStatus.Error(
                "Pilih lampiran terlebih dahulu.",
                null
            )
            return
        }

        simulateExtractContent(attachment) { title, content, topic ->
            _userNotice.value = "Konten lampiran berhasil diproses."
        }
    }

    fun simulateExtractContent(
        attachment: SavedAttachment,
        onExtracted: (
            extractedTitle: String,
            extractedContent: String,
            extractedTopic: String
        ) -> Unit
    ) {
        viewModelScope.launch {
            _extractionStatus.value = ExtractionStatus.Extracting
            delay(1200)

            val formattedTitle = attachment.displayName
                .substringBeforeLast(".")
                .replace("_", " ")
                .replace("-", " ")
                .split(" ")
                .filter { it.isNotBlank() }
                .joinToString(" ") {
                    it.replaceFirstChar { c -> c.uppercase() }
                }

            val topic = if (
                attachment.displayName.endsWith(".pdf", ignoreCase = true)
            ) {
                "Dokumen Belajar Digital"
            } else {
                "Catatan Visual"
            }

            val content = when (attachment.type) {
                "IMAGE" -> """
                    # $formattedTitle

                    ## Ringkasan Catatan Visual

                    Materi ini berasal dari lampiran gambar
                    ${attachment.displayName}.

                    ### Poin-Poin Utama
                    - Lampiran tersimpan di penyimpanan internal aplikasi.
                    - Periksa gambar asli untuk membaca isi secara akurat.
                    - Tambahkan ringkasan materi pada editor.
                """.trimIndent()

                else -> """
                    # $formattedTitle

                    ## Intisari Berkas Dokumen

                    Dokumen ${attachment.displayName} telah ditautkan.

                    ### Pembahasan Materi
                    - Nama berkas: ${attachment.displayName}
                    - Tipe berkas: ${attachment.type}
                    - Tambahkan ringkasan isi dokumen pada editor.
                """.trimIndent()
            }

            _extractionStatus.value = ExtractionStatus.Success(
                "Konten berhasil diolah dari lampiran."
            )

            onExtracted(formattedTitle, content, topic)
        }
    }

    fun clearExtractionStatus() {
        _extractionStatus.value = ExtractionStatus.Idle
    }

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

        val attachment = _activeDraftAttachment.value

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
                attachmentName = attachment?.displayName,
                attachmentNote = attachment?.let {
                    "${it.type} (${it.sizeBytes / 1024} KB)"
                },
                attachmentPath = attachment?.localPath,
                attachmentType = attachment?.type
            )

            _activeDraftAttachment.value = null
            _extractionStatus.value = ExtractionStatus.Idle
            _userNotice.value = if (isDraft) {
                "Draft materi berhasil disimpan."
            } else {
                "Materi baru berhasil diterbitkan!"
            }

            onSuccess()
        }
    }

    fun deleteMaterial(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMaterial(id)
            _userNotice.value = "Materi telah dihapus."
        }
    }

    fun claimQuest(quest: QuestEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.claimQuest(quest.id)
            _userNotice.value = if (success) {
                "Berhasil mengklaim +${quest.xpReward} XP untuk quest: ${quest.title}!"
            } else {
                "Quest belum selesai atau sudah pernah diklaim sebelumnya."
            }
        }
    }

    fun updateProfile(name: String, level: String, grade: Int) {
        if (name.isBlank()) {
            _userNotice.value = "Nama profil tidak boleh kosong."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(name.trim(), level, grade)
            _userNotice.value = "Profil berhasil diperbarui."
        }
    }

    fun triggerSyncCheck() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateSyncStatus("LOCAL_ONLY")
            _userNotice.value =
                "Data lokal sudah diperiksa. Sinkronisasi cloud belum tersedia."
        }
    }

    fun resetData() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetLearningData()
            _examSession.value = ExamSessionState()
            _activeReadingMaterial.value = null
            _activeSibiBook.value = null
            _activeDraftAttachment.value = null
            _extractionStatus.value = ExtractionStatus.Idle
        }
    }
}
