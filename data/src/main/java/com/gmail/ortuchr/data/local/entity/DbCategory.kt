package com.gmail.ortuchr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Category")
data class DbCategory(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val checked: Int
)