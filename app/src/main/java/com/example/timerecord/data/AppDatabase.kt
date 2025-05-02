package com.example.timerecord.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Record::class, Tag::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
    abstract fun tagDao(): TagDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 创建临时表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS tags_temp (
                        name TEXT NOT NULL PRIMARY KEY
                    )
                """)

                // 从旧表复制数据到临时表
                database.execSQL("""
                    INSERT INTO tags_temp (name)
                    SELECT name FROM tags
                """)

                // 删除旧表
                database.execSQL("DROP TABLE tags")

                // 重命名临时表
                database.execSQL("ALTER TABLE tags_temp RENAME TO tags")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 