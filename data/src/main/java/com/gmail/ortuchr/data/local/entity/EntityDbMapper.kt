package com.gmail.ortuchr.data.local.entity

import com.gmail.ortuchr.domain.entity.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

fun setDateSaveFormat(date: String): String {
    var dateFormat = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
    val newDate = dateFormat.parse(date)
    dateFormat = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH)
    return dateFormat.format(newDate)
}

fun setDataDisplayFormat(date: String): String {
    var dateFormat = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH)
    val newDate = dateFormat.parse(date)
    dateFormat = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
    return dateFormat.format(newDate)
}

fun DbBalance.transformToDomain(): Balance {
    return Balance(
        id = id.toString(),
        amount = if (type != "income" && type != "transfer") -amount.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
        else amount.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY),
        date = setDataDisplayFormat(date),
        type = type,
        account = account,
        comment = comment
    )
}

fun Balance.transformToDb(): DbBalance {
    return DbBalance(
        id = id.toInt(),
        amount = if (type != "income" && type != "transfer") -amount.multiply(BigDecimal("100")).toInt()
        else amount.multiply(BigDecimal("100")).toInt(),
        date = setDateSaveFormat(date),
        type = type,
        account = account,
        comment = comment
    )
}

fun BalanceAdd.transformToDb(): DbBalance {
    return DbBalance(
        amount = if (type != "income" && type != "transfer") -amount.multiply(BigDecimal("100")).toInt()
        else amount.multiply(BigDecimal("100")).toInt(),
        date = setDateSaveFormat(date),
        type = type,
        account = account,
        comment = comment
    )
}

fun DbDebt.transformToDomain(): Debt {
    return Debt(
        id = id.toString(),
        amount = if (type == "toMe") -amount.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
        else amount.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY),
        debtor = debtor,
        dateStart = setDataDisplayFormat(dateStart),
        dateFinish = setDataDisplayFormat(dateFinish),
        type = type,
        account = account,
        comment = comment
    )
}

fun Debt.transformToDb(): DbDebt {
    return DbDebt(
        id = id.toInt(),
        amount = if (type == "toMe") -amount.multiply(BigDecimal("100")).toInt()
        else amount.multiply(BigDecimal("100")).toInt(),
        debtor = debtor,
        dateStart = setDateSaveFormat(dateStart),
        dateFinish = setDateSaveFormat(dateFinish),
        type = type,
        account = account,
        comment = comment
    )
}

fun DebtAdd.transformToDb(): DbDebt {
    return DbDebt(
        amount = if (type == "toMe") -amount.multiply(BigDecimal("100")).toInt()
        else amount.multiply(BigDecimal("100")).toInt(),
        debtor = debtor,
        dateStart = setDateSaveFormat(dateStart),
        dateFinish = setDateSaveFormat(dateFinish),
        type = type,
        account = account,
        comment = comment
    )
}

fun DbSetting.transformToDomain(): Setting {
    return Setting(
        id = id.toString(),
        value = value
    )
}

fun Setting.transformToDb(): DbSetting {
    return DbSetting(
        id = id.toInt(),
        value = value
    )
}

fun DbCategory.transformToDomain(): Category {
    return Category(
        id = id.toString(),
        title = title,
        checked = checked
    )
}

fun Category.transformToDb(): DbCategory {
    return DbCategory(
        id = id.toInt(),
        title = title,
        checked = checked
    )
}