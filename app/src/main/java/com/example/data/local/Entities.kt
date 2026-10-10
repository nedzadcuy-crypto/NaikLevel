package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val content: String,
    val schoolLevel: String, // "SD", "SMP", "SMA", "SMK"
    val grade: Int, // 1 to 12
    val subject: String, // "Matematika", "IPA", "IPS", etc.
    val topic: String,
    val source: String, // "KURIKULUM_NASIONAL", "USER_CREATED"
    val estimatedReadMinutes: Int = 5,
    val readProgress: Float = 0f, // 0.0f to 1.0f
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val isDraft: Boolean = false,
    val attachmentName: String? = null,
    val originalAttachmentNote: String? = null,
    val attachmentPath: String? = null, // Local absolute path to stored image or document
    val attachmentType: String? = null  // "IMAGE", "DOCUMENT", null
)

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val userName: String = "Pelajar Indonesia",
    val schoolLevel: String = "SMA",
    val grade: Int = 10,
    val xp: Int = 0,
    val streakDays: Int = 0,
    val lastActiveDate: String = "", // "YYYY-MM-DD"
    val syncStatus: String = "LOCAL_ONLY" // "LOCAL_ONLY", "SYNCED", "PENDING"
)

@Entity(tableName = "quests")
data class QuestEntity(
    @PrimaryKey
    val id: String, // e.g. "daily_read_2026-10-09"
    val title: String,
    val description: String,
    val type: String, // "DAILY", "WEEKLY"
    val targetCount: Int,
    val currentCount: Int = 0,
    val xpReward: Int,
    val isClaimed: Boolean = false,
    val periodKey: String // Date or Week identifier
)

@Entity(tableName = "exam_attempts")
data class ExamAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val subject: String,
    val schoolLevel: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val score: Int, // 0 - 100
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceMaterialTitle: String? = null,
    val isCustomExam: Boolean = false,
    val answersJson: String? = null,
    val reviewSummaryJson: String? = null
)

@Entity(tableName = "xp_transactions")
data class XpTransactionEntity(
    @PrimaryKey
    val actionKey: String, // Unique key for idempotency: e.g. "materi_complete_1", "quest_daily_read_2026-10-09"
    val xpGained: Int,
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)
