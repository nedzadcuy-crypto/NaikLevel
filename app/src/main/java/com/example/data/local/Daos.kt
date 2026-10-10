package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialDao {
    @Query("SELECT * FROM materials WHERE isDraft = 0 ORDER BY lastAccessedAt DESC")
    fun getAllPublishedMaterials(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE isDraft = 1 ORDER BY lastAccessedAt DESC")
    fun getDraftMaterials(): Flow<List<MaterialEntity>>

    @Query("SELECT * FROM materials WHERE id = :id")
    fun getMaterialById(id: Long): Flow<MaterialEntity?>

    @Query("SELECT * FROM materials WHERE id = :id")
    suspend fun getMaterialByIdOnce(id: Long): MaterialEntity?

    @Query("SELECT * FROM materials WHERE isDraft = 0 AND (readProgress > 0 OR isCompleted = 1) ORDER BY lastAccessedAt DESC LIMIT 1")
    fun getMostRecentStudyMaterial(): Flow<MaterialEntity?>

    @Query("SELECT COUNT(*) FROM materials WHERE isCompleted = 1")
    fun getCompletedMaterialsCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM materials")
    suspend fun getTotalMaterialsCountOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: MaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMaterials(materials: List<MaterialEntity>): List<Long>

    @Update
    suspend fun updateMaterial(material: MaterialEntity)

    @Query("DELETE FROM materials WHERE id = :id")
    suspend fun deleteMaterialById(id: Long)

    @Query("UPDATE materials SET readProgress = :progress, isCompleted = :isCompleted, completedAt = :completedAt, lastAccessedAt = :now WHERE id = :id")
    suspend fun updateReadingProgress(id: Long, progress: Float, isCompleted: Boolean, completedAt: Long?, now: Long)
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE id = 1")
    suspend fun getUserProgressOnce(): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET xp = xp + :amount WHERE id = 1")
    suspend fun incrementXp(amount: Int)

    @Query("UPDATE user_progress SET streakDays = :streak, lastActiveDate = :date WHERE id = 1")
    suspend fun updateStreak(streak: Int, date: String)

    @Query("UPDATE user_progress SET schoolLevel = :level, grade = :grade WHERE id = 1")
    suspend fun updateSchoolLevel(level: String, grade: Int)

    @Query("UPDATE user_progress SET userName = :name WHERE id = 1")
    suspend fun updateUserName(name: String)

    @Query("UPDATE user_progress SET syncStatus = :status WHERE id = 1")
    suspend fun updateSyncStatus(status: String)
}

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY type ASC, xpReward ASC")
    fun getAllQuests(): Flow<List<QuestEntity>>

    @Query("SELECT * FROM quests WHERE id = :id")
    suspend fun getQuestById(id: String): QuestEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuests(quests: List<QuestEntity>)

    @Update
    suspend fun updateQuest(quest: QuestEntity)

    @Query("UPDATE quests SET currentCount = currentCount + :amount WHERE id = :id")
    suspend fun incrementQuestProgress(id: String, amount: Int)

    @Query("UPDATE quests SET isClaimed = 1 WHERE id = :id")
    suspend fun markQuestClaimed(id: String)
}

@Dao
interface ExamAttemptDao {
    @Query("SELECT * FROM exam_attempts ORDER BY timestamp DESC")
    fun getAllExamAttempts(): Flow<List<ExamAttemptEntity>>

    @Query("SELECT COUNT(*) FROM exam_attempts")
    fun getTotalExamsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: ExamAttemptEntity): Long

    @Query("DELETE FROM exam_attempts")
    suspend fun clearHistory()
}

@Dao
interface XpTransactionDao {
    @Query("SELECT COUNT(*) FROM xp_transactions WHERE actionKey = :actionKey")
    suspend fun hasTransaction(actionKey: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTransaction(transaction: XpTransactionEntity): Long

    @Query("SELECT * FROM xp_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<XpTransactionEntity>>
}
