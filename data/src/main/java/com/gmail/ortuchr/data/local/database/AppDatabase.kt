package com.gmail.ortuchr.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gmail.ortuchr.data.local.dao.FinanceDao
import com.gmail.ortuchr.data.local.entity.DbBalance
import com.gmail.ortuchr.data.local.entity.DbCategory
import com.gmail.ortuchr.data.local.entity.DbDebt
import com.gmail.ortuchr.data.local.entity.DbSetting

@Database(
    entities = [DbBalance::class, DbDebt::class, DbSetting::class, DbCategory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        private const val THIS_NAME = "my_table"

        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                THIS_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun getFinanceDao(): FinanceDao
}