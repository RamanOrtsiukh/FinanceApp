package com.gmail.ortuchr.domain.entity

import java.math.BigDecimal

data class Balance(
    val id: String,
    val amount: BigDecimal,
    val date: String,
    val type: String,
    val account: String,
    val comment: String
) : DomainEntity