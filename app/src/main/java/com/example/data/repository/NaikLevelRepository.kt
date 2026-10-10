package com.example.data.repository

import com.example.data.local.ExamAttemptDao
import com.example.data.local.ExamAttemptEntity
import com.example.data.local.MaterialDao
import com.example.data.local.MaterialEntity
import com.example.data.local.QuestDao
import com.example.data.local.QuestEntity
import com.example.data.local.UserProgressDao
import com.example.data.local.UserProgressEntity
import com.example.data.local.XpTransactionDao
import com.example.data.local.XpTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NaikLevelRepository(
    private val materialDao: MaterialDao,
    private val userProgressDao: UserProgressDao,
    private val questDao: QuestDao,
    private val examAttemptDao: ExamAttemptDao,
    private val xpTransactionDao: XpTransactionDao
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val publishedMaterials: Flow<List<MaterialEntity>> = materialDao.getAllPublishedMaterials()
    val draftMaterials: Flow<List<MaterialEntity>> = materialDao.getDraftMaterials()
    val recentMaterial: Flow<MaterialEntity?> = materialDao.getMostRecentStudyMaterial()
    val completedMaterialsCount: Flow<Int> = materialDao.getCompletedMaterialsCount()
    val userProgress: Flow<UserProgressEntity?> = userProgressDao.getUserProgress()
    val allQuests: Flow<List<QuestEntity>> = questDao.getAllQuests()
    val examAttempts: Flow<List<ExamAttemptEntity>> = examAttemptDao.getAllExamAttempts()
    val xpTransactions: Flow<List<XpTransactionEntity>> = xpTransactionDao.getAllTransactions()

    suspend fun getMaterialById(id: Long): MaterialEntity? = materialDao.getMaterialByIdOnce(id)

    /**
     * Idempotently awards XP.
     * Guarantees that repeating an action key never duplicates XP.
     */
    suspend fun awardXp(actionKey: String, amount: Int, reason: String): Boolean {
        if (amount <= 0) return false
        val existing = xpTransactionDao.hasTransaction(actionKey)
        if (existing > 0) {
            return false // Already rewarded
        }
        val tx = XpTransactionEntity(
            actionKey = actionKey,
            xpGained = amount,
            reason = reason,
            timestamp = System.currentTimeMillis()
        )
        val inserted = xpTransactionDao.insertTransaction(tx)
        if (inserted > 0) {
            userProgressDao.incrementXp(amount)
            return true
        }
        return false
    }

    suspend fun checkAndUpdateDailyStreak() {
        val todayStr = dateFormat.format(Date())
        val currentProgress = userProgressDao.getUserProgressOnce() ?: return

        if (currentProgress.lastActiveDate.isBlank()) {
            userProgressDao.updateStreak(1, todayStr)
            return
        }

        if (currentProgress.lastActiveDate == todayStr) {
            // Already active today
            return
        }

        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        if (currentProgress.lastActiveDate == yesterdayStr) {
            val newStreak = currentProgress.streakDays + 1
            userProgressDao.updateStreak(newStreak, todayStr)
            if (newStreak >= 3) {
                questDao.incrementQuestProgress("weekly_streak_3", 1)
            }
        } else {
            // Gap day, streak resets to 1
            userProgressDao.updateStreak(1, todayStr)
        }
    }

    suspend fun updateReadingProgress(materialId: Long, progress: Float) {
        val mat = materialDao.getMaterialByIdOnce(materialId) ?: return
        val clampedProgress = progress.coerceIn(0f, 1f)
        val isNowCompleted = clampedProgress >= 0.95f
        val completedAt = if (isNowCompleted && !mat.isCompleted) System.currentTimeMillis() else mat.completedAt

        materialDao.updateReadingProgress(
            id = materialId,
            progress = clampedProgress,
            isCompleted = isNowCompleted,
            completedAt = completedAt,
            now = System.currentTimeMillis()
        )

        if (isNowCompleted && !mat.isCompleted) {
            // Award XP for completing material (Idempotent per material ID)
            awardXp(
                actionKey = "complete_material_$materialId",
                amount = 40,
                reason = "Menyelesaikan materi: ${mat.title}"
            )
            questDao.incrementQuestProgress("daily_read_material", 1)
            questDao.incrementQuestProgress("weekly_complete_3_materials", 1)
        }
    }

    suspend fun saveMaterial(
        title: String,
        description: String,
        content: String,
        schoolLevel: String,
        grade: Int,
        subject: String,
        topic: String,
        isDraft: Boolean,
        attachmentName: String?,
        attachmentNote: String?,
        attachmentPath: String? = null,
        attachmentType: String? = null
    ): Long {
        val entity = MaterialEntity(
            title = title,
            description = description,
            content = content,
            schoolLevel = schoolLevel,
            grade = grade,
            subject = subject,
            topic = topic,
            source = "USER_CREATED",
            estimatedReadMinutes = maxOf(2, content.length / 500),
            isDraft = isDraft,
            attachmentName = attachmentName,
            originalAttachmentNote = attachmentNote,
            attachmentPath = attachmentPath,
            attachmentType = attachmentType,
            lastAccessedAt = System.currentTimeMillis()
        )
        return materialDao.insertMaterial(entity)
    }

    suspend fun deleteMaterial(id: Long) {
        val existing = materialDao.getMaterialByIdOnce(id)
        if (existing?.attachmentPath != null) {
            com.example.data.local.AttachmentManager.deleteAttachment(existing.attachmentPath)
        }
        materialDao.deleteMaterialById(id)
    }

    suspend fun recordExamAttempt(
        subject: String,
        schoolLevel: String,
        totalQuestions: Int,
        correctAnswers: Int,
        durationSeconds: Int,
        sourceMaterialTitle: String? = null,
        isCustomExam: Boolean = false,
        answersJson: String? = null,
        reviewSummaryJson: String? = null
    ): Long {
        val score = if (totalQuestions > 0) (correctAnswers * 100) / totalQuestions else 0
        val timestamp = System.currentTimeMillis()
        val attempt = ExamAttemptEntity(
            subject = subject,
            schoolLevel = schoolLevel,
            totalQuestions = totalQuestions,
            correctAnswers = correctAnswers,
            score = score,
            durationSeconds = durationSeconds,
            timestamp = timestamp,
            sourceMaterialTitle = sourceMaterialTitle,
            isCustomExam = isCustomExam,
            answersJson = answersJson,
            reviewSummaryJson = reviewSummaryJson
        )
        val id = examAttemptDao.insertAttempt(attempt)

        // Award XP for taking exam idempotently
        val xpGain = 30 + (score / 2) // base 30 XP + up to 50 XP based on score
        awardXp(
            actionKey = "exam_attempt_${timestamp}_$id",
            amount = xpGain,
            reason = "Menyelesaikan Ujian $subject ($score%)"
        )
        questDao.incrementQuestProgress("daily_complete_exam", 1)
        return id
    }

    suspend fun claimQuest(questId: String): Boolean {
        val quest = questDao.getQuestById(questId) ?: return false
        if (quest.isClaimed || quest.currentCount < quest.targetCount) return false

        val success = awardXp(
            actionKey = "claim_quest_${quest.id}_${quest.periodKey}",
            amount = quest.xpReward,
            reason = "Hadiah Quest: ${quest.title}"
        )
        if (success) {
            questDao.markQuestClaimed(questId)
        }
        return success
    }

    suspend fun onAiTutorQueried() {
        questDao.incrementQuestProgress("daily_ask_ai_tutor", 1)
        awardXp(
            actionKey = "ai_tutor_first_${dateFormat.format(Date())}",
            amount = 15,
            reason = "Interaksi dengan Tutor AI NaikLevel"
        )
    }

    suspend fun updateProfile(name: String, level: String, grade: Int) {
        userProgressDao.updateUserName(name)
        userProgressDao.updateSchoolLevel(level, grade)
    }

    suspend fun updateSyncStatus(status: String) {
        userProgressDao.updateSyncStatus(status)
    }

    suspend fun resetLearningData() {
        examAttemptDao.clearHistory()
        // Reset user progress
        userProgressDao.insertOrUpdate(
            UserProgressEntity(
                id = 1,
                userName = "Pelajar Indonesia",
                schoolLevel = "SMA",
                grade = 10,
                xp = 0,
                streakDays = 1,
                lastActiveDate = dateFormat.format(Date()),
                syncStatus = "LOCAL_ONLY"
            )
        )
    }
}
