package com.gmail.ortuchr.data.repositories

import com.gmail.ortuchr.data.local.dao.FinanceDao
import com.gmail.ortuchr.data.local.entity.setDateSaveFormat
import com.gmail.ortuchr.data.local.entity.transformToDb
import com.gmail.ortuchr.data.local.entity.transformToDomain
import com.gmail.ortuchr.domain.entity.*
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import java.math.BigDecimal

class FinanceRepositoryImpl(private val financeDao: FinanceDao) : FinanceRepository {

    override fun addBalance(balanceAdd: BalanceAdd): Completable {
        return Completable.fromAction {
            financeDao.addBalance(balanceAdd.transformToDb())
        }
    }

    override fun updateBalance(balanceUpdate: Balance): Completable {
        return Completable.fromAction {
            financeDao.updateBalance(balanceUpdate.transformToDb())
        }
    }

    override fun deleteBalance(balanceDelete: Balance): Completable {
        return Completable.fromAction {
            financeDao.deleteBalance(balanceDelete.id)
        }
    }

    override fun getOneBalance(balanceGet: Balance): Flowable<Balance> {
        return financeDao.getOneBalance(balanceGet.id)
            .take(1)
            .map {
                it.transformToDomain()
            }
    }

    override fun addDebt(debtAdd: DebtAdd): Completable {
        return Completable.fromAction {
            financeDao.addDebt(debtAdd.transformToDb())
        }
    }

    override fun updateDebt(debtUpdate: Debt): Completable {
        return Completable.fromAction {
            financeDao.updateDebt(debtUpdate.transformToDb())
        }
    }

    override fun deleteDebt(debtDelete: Debt): Completable {
        return Completable.fromAction {
            financeDao.deleteDebt(debtDelete.id)
        }
    }

    override fun getOneDebt(debtGet: Debt): Flowable<Debt> {
        return financeDao.getOneDebt(debtGet.id)
            .take(1)
            .map {
                it.transformToDomain()
            }
    }

    override fun saveSetting(settingSave: Setting): Completable {
        return Completable.fromAction {
            financeDao.saveSetting(settingSave.transformToDb())
        }
    }

    override fun loadSetting(settingLoad: Setting): Flowable<Setting> {
        return financeDao.loadSetting(settingLoad.id)
            .take(1)
            .map {
                it.transformToDomain()
            }
    }

    override fun saveCategory(categorySave: Category): Completable {
        return Completable.fromAction {
            financeDao.saveCategory(categorySave.transformToDb())
        }
    }

    override fun updateCategoryTitle(categoryUpdate: Category): Completable {
        return Completable.fromAction {
            financeDao.updateCategoryTitle(categoryUpdate.transformToDb().id, categoryUpdate.title)
        }
    }

    override fun updateCategoryChecked(categoryUpdate: Category): Completable {
        return Completable.fromAction {
            financeDao.updateCategoryChecked(categoryUpdate.transformToDb().id, categoryUpdate.checked)
        }
    }

    override fun changeCategoryInRecords(categoryPrevious: Category, categoryChange: Category): Completable {
        return Completable.fromAction {
            financeDao.changeCategoryInRecords(categoryPrevious.title, categoryChange.title)
        }
    }

    override fun loadCategory(categoryLoad: Category): Flowable<Category> {
        return financeDao.loadCategory(categoryLoad.id)
            .take(1)
            .map {
                it.transformToDomain()
            }
    }

    override fun deleteAllBalance(): Completable {
        return Completable.fromAction {
            financeDao.deleteAllBalance()
        }
    }

    override fun deleteAllDebts(): Completable {
        return Completable.fromAction {
            financeDao.deleteAllDebts()
        }
    }

    override fun getSymBalance(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymBalance(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymBalanceCard(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymBalanceCard(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymBalanceCash(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymBalanceCash(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymBalanceFromList(fromDate: String, toDate: String, list: List<String>): Flowable<BigDecimal> {
        return financeDao.getSymBalanceFromList(setDateSaveFormat(fromDate), setDateSaveFormat(toDate), list)
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymIncome(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymIncome(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymExpenses(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymExpenses(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymExpensesCard(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymExpensesCard(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymExpensesCash(fromDate: String, toDate: String): Flowable<BigDecimal> {
        return financeDao.getSymExpensesCash(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymExpensesFromType(fromDate: String, toDate: String, type: String): Flowable<BigDecimal> {
        return financeDao.getSymExpensesFromType(setDateSaveFormat(fromDate), setDateSaveFormat(toDate), type)
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymExpensesFromList(fromDate: String, toDate: String, list: List<String>): Flowable<BigDecimal> {
        return financeDao.getSymExpensesFromList(setDateSaveFormat(fromDate), setDateSaveFormat(toDate), list)
            .take(1)
            .map {
                it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
            }
    }

    override fun getSymDebts(type: String, fromDate: String, toDate: String): Flowable<BigDecimal> {
        return when (type.isEmpty()) {
            true -> {
                financeDao.getSymDebtsAll(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
            else -> {
                financeDao.getSymDebts(type, setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
        }
    }

    override fun getSymDebtsCard(type: String, fromDate: String, toDate: String): Flowable<BigDecimal> {
        return when (type.isEmpty()) {
            true -> {
                financeDao.getSymDebtsCardAll(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
            else -> {
                financeDao.getSymDebtsCard(type, setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
        }
    }

    override fun getSymDebtsCash(type: String, fromDate: String, toDate: String): Flowable<BigDecimal> {
        return when (type.isEmpty()) {
            true -> {
                financeDao.getSymDebtsCashAll(setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
            else -> {
                financeDao.getSymDebtsCash(type, setDateSaveFormat(fromDate), setDateSaveFormat(toDate))
                    .take(1)
                    .map {
                        it.toBigDecimal().divide(BigDecimal("100"), 2, BigDecimal.ROUND_UNNECESSARY)
                    }
            }
        }
    }

    override fun getAllBalance(fromDate: String, toDate: String, list: List<String>): Flowable<List<Balance>> {
        return financeDao.getAllBalance(setDateSaveFormat(fromDate), setDateSaveFormat(toDate), list)
            .take(1)
            .map { listDbBalance ->
                listDbBalance
                    .map { DbBalance ->
                        DbBalance.transformToDomain()
                    }
            }
    }

    override fun getAllDebts(type: String): Flowable<List<Debt>> {
        return financeDao.getAllDebts(type)
            .take(1)
            .map { listDbDebts ->
                listDbDebts
                    .map { DbDebt ->
                        DbDebt.transformToDomain()
                    }
            }
    }
}