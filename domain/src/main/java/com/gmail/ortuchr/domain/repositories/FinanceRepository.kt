package com.gmail.ortuchr.domain.repositories

import com.gmail.ortuchr.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Flowable
import java.math.BigDecimal

interface FinanceRepository: BaseRepository {

    fun addBalance(balanceAdd: BalanceAdd) : Completable
    fun updateBalance(balanceUpdate: Balance) : Completable
    fun deleteBalance(balanceDelete: Balance) : Completable
    fun getOneBalance(balanceGet: Balance) : Flowable<Balance>

    fun addDebt(debtAdd: DebtAdd) : Completable
    fun updateDebt(debtUpdate: Debt) : Completable
    fun deleteDebt(debtDelete: Debt) : Completable
    fun getOneDebt(debtGet: Debt) : Flowable<Debt>

    fun saveSetting(settingSave: Setting) : Completable
    fun loadSetting(settingLoad: Setting) : Flowable<Setting>

    fun saveCategory(categorySave: Category) : Completable
    fun updateCategoryTitle(categoryUpdate: Category) : Completable
    fun updateCategoryChecked(categoryUpdate: Category) : Completable
    fun changeCategoryInRecords(categoryPrevious: Category, categoryChange: Category) : Completable
    fun loadCategory(categoryLoad: Category) : Flowable<Category>

    fun deleteAllBalance() : Completable
    fun deleteAllDebts() : Completable

    fun getSymBalance(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymBalanceCard(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymBalanceCash(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymBalanceFromList(fromDate: String, toDate: String, list: List<String>) : Flowable<BigDecimal>
    fun getSymIncome(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymExpenses(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymExpensesCard(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymExpensesCash(fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymExpensesFromType(fromDate: String, toDate: String, type: String) : Flowable<BigDecimal>
    fun getSymExpensesFromList(fromDate: String, toDate: String, list: List<String>) : Flowable<BigDecimal>
    fun getSymDebts(type: String, fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymDebtsCard(type: String, fromDate: String, toDate: String) : Flowable<BigDecimal>
    fun getSymDebtsCash(type: String, fromDate: String, toDate: String) : Flowable<BigDecimal>

    fun getAllBalance(fromDate: String, toDate: String, list: List<String>) : Flowable<List<Balance>>
    fun getAllDebts(type: String) : Flowable<List<Debt>>
}