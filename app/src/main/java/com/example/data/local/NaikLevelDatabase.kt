package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MaterialEntity::class,
        UserProgressEntity::class,
        QuestEntity::class,
        ExamAttemptEntity::class,
        XpTransactionEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class NaikLevelDatabase : RoomDatabase() {
    abstract fun materialDao(): MaterialDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun questDao(): QuestDao
    abstract fun examAttemptDao(): ExamAttemptDao
    abstract fun xpTransactionDao(): XpTransactionDao

    companion object {
        @Volatile
        private var INSTANCE: NaikLevelDatabase? = null

        fun getInstance(context: Context): NaikLevelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NaikLevelDatabase::class.java,
                    "naiklevel_database"
                )
                    // Hanya DB versi 1 (pra-rilis) yang boleh direset. Versi berikutnya WAJIB punya Migration eksplisit;
                    // tanpa itu aplikasi gagal terang-terangan, bukan menghapus data pengguna secara diam-diam.
                    .fallbackToDestructiveMigrationFrom(true, 1)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
