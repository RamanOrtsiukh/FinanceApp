package com.gmail.ortuchr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Settings")
data class DbSetting(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val value: Int
)