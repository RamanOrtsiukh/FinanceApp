package com.gmail.ortuchr.finance.presentation.utils

import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

fun setMoneyDisplayFormat(amount: BigDecimal): String {
    return String.format(Locale.ENGLISH, "%.2f", amount)
}

fun setPercentsDisplayFormat(amount: BigDecimal): String {
    return String.format(Locale.ENGLISH, "%.1f", amount)  + "%"
}

fun setTodayDataDisplayFormat(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
    return dateFormat.format(date)
}