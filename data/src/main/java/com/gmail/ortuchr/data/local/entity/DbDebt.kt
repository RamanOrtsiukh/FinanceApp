package com.gmail.ortuchr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Debts")
data class DbDebt(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val amount: Int,
    val debtor: String,
    val dateStart: String,
    val dateFinish: String,
    val type: String,
    val account: String,
    val comment: String
)