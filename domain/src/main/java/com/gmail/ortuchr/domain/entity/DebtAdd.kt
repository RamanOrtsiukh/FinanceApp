package com.gmail.ortuchr.domain.entity

import java.math.BigDecimal

data class DebtAdd(
    val amount: BigDecimal,
    val debtor: String,
    val dateStart: String,
    val dateFinish: String,
    val type: String,
    val account: String,
    val comment: String
) : DomainEntity