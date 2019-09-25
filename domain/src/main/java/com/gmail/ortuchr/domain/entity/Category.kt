package com.gmail.ortuchr.domain.entity

data class Category(
    val id: String,
    val title: String,
    val checked: Int
) : DomainEntity