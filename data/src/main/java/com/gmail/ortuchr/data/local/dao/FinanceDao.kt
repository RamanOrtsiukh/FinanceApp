package com.gmail.ortuchr.data.local.dao

import androidx.room.*
import com.gmail.ortuchr.data.local.entity.DbBalance
import com.gmail.ortuchr.data.local.entity.DbCategory
import com.gmail.ortuchr.data.local.entity.DbDebt
import com.gmail.ortuchr.data.local.entity.DbSetting
import io.reactivex.Flowable

@Dao
interface FinanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addBalance(balanceAdd: DbBalance)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateBalance(balanceUpdate: DbBalance)

    @Query("DELETE FROM Balance WHERE id = :idDelete")
    fun deleteBalance(idDelete: String)

    @Query("SELECT * FROM Balance WHERE id = :idGet LIMIT 1")
    fun getOneBalance(idGet: String): Flowable<DbBalance>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDebt(debtAdd: DbDebt)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateDebt(debtUpdate: DbDebt)

    @Query("DELETE FROM Debts WHERE id = :idDelete")
    fun deleteDebt(idDelete: String)

    @Query("SELECT * FROM Debts WHERE id = :idGet LIMIT 1")
    fun getOneDebt(idGet: String): Flowable<DbDebt>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSetting(settingSave: DbSetting)

    @Query("SELECT * FROM Settings WHERE id = :idLoad LIMIT 1")
    fun loadSetting(idLoad: String): Flowable<DbSetting>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCategory(categorySave: DbCategory)

    @Query("UPDATE Category SET title = :newTitle WHERE id = :idCategory")
    fun updateCategoryTitle(idCategory: Int, newTitle: String)

    @Query("UPDATE Category SET checked = :checked WHERE id = :idCategory")
    fun updateCategoryChecked(idCategory: Int, checked: Int)

    @Query("UPDATE Balance SET type = :titleChange WHERE type = :titlePrevious")
    fun changeCategoryInRecords(titlePrevious: String, titleChange: String)

    @Query("SELECT * FROM Category WHERE id = :idLoad LIMIT 1")
    fun loadCategory(idLoad: String) : Flowable<DbCategory>


    @Query("DELETE FROM Balance")
    fun deleteAllBalance()

    @Query("DELETE FROM Debts")
    fun deleteAllDebts()


    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate")
    fun getSymBalance(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'card'")
    fun getSymBalanceCard(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'cash'")
    fun getSymBalanceCash(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type IN (:list)")
    fun getSymBalanceFromList(fromDate: String, toDate: String, list: List<String>): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type = 'income'")
    fun getSymIncome(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'card' AND type = 'income'")
    fun getSymIncomeCard(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'cash' AND type = 'income'")
    fun getSymIncomeCash(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type != 'income' AND type != 'transfer'")
    fun getSymExpenses(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'card' AND type != 'income' AND type != 'transfer'")
    fun getSymExpensesCard(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND account = 'cash' AND type != 'income' AND type != 'transfer'")
    fun getSymExpensesCash(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type = :type")
    fun getSymExpensesFromType(fromDate: String, toDate: String, type: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type != 'income' AND type != 'transfer' AND type IN (:list)")
    fun getSymExpensesFromList(fromDate: String, toDate: String, list: List<String>): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate")
    fun getSymDebtsAll(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate AND type = :type")
    fun getSymDebts(type: String, fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate AND account = 'card'")
    fun getSymDebtsCardAll(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate AND type = :type AND account = 'card'")
    fun getSymDebtsCard(type: String, fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate AND account = 'cash'")
    fun getSymDebtsCashAll(fromDate: String, toDate: String): Flowable<Int>

    @Query("SELECT SUM(amount) FROM Debts WHERE dateStart >= :fromDate AND dateStart <= :toDate AND type = :type AND account = 'cash'")
    fun getSymDebtsCash(type: String, fromDate: String, toDate: String): Flowable<Int>


    @Query("SELECT * FROM Balance WHERE date >= :fromDate AND date <= :toDate AND type IN (:list) ORDER BY date DESC")
    fun getAllBalance(fromDate: String, toDate: String, list: List<String>): Flowable<List<DbBalance>>

    @Query("SELECT * FROM Debts WHERE type = :type order by dateStart ASC")
    fun getAllDebts(type: String): Flowable<List<DbDebt>>
}