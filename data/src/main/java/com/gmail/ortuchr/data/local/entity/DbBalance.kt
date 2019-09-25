package com.gmail.ortuchr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Balance")
data class DbBalance(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Int,
    val date: String,
    val type: String,
    val account: String,
    val comment: String
)