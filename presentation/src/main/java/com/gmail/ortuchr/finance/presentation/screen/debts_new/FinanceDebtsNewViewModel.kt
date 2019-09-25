package com.gmail.ortuchr.finance.presentation.screen.debts_new

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.domain.entity.DebtAdd
import com.gmail.ortuchr.domain.usecases.debt.AddDebtUseCase
import com.gmail.ortuchr.domain.usecases.debt.DeleteDebtUseCase
import com.gmail.ortuchr.domain.usecases.debt.GetOneDebtUseCase
import com.gmail.ortuchr.domain.usecases.debt.UpdateDebtUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.base.BaseViewModel
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat
import com.gmail.ortuchr.finance.presentation.utils.setTodayDataDisplayFormat
import io.reactivex.rxkotlin.subscribeBy
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

class FinanceDebtsNewViewModel : BaseViewModel<FinanceRouter>() {

    private lateinit var idDebt: String
    val isNewDebt = ObservableBoolean(true)
    val amountDebt = ObservableField<String>("")
    val debtor = ObservableField<String>("")
    val dateStart = ObservableField<String>("")
    val dateFinish = ObservableField<String>("")
    val isFromMe = ObservableBoolean(true)
    val isToMe = ObservableBoolean(false)
    val isCard = ObservableBoolean(true)
    val isCash = ObservableBoolean(false)
    val commentDebt = ObservableField<String>("")

    @Inject
    lateinit var getOneDebtUseCase: GetOneDebtUseCase
    @Inject
    lateinit var addDebtUseCase: AddDebtUseCase
    @Inject
    lateinit var updateDebtUseCase: UpdateDebtUseCase
    @Inject
    lateinit var deleteDebtUseCase: DeleteDebtUseCase

    init {
        App.appComponent.inject(this)
    }

    //заполнение данных
    fun isNewDebt(id: String) {
        idDebt = id
        if (idDebt.isEmpty()) {
            isNewDebt.set(true)
            dateStart.set(setTodayDataDisplayFormat(Date()))
            dateFinish.set(setTodayDataDisplayFormat(Date()))
        } else {
            isNewDebt.set(false)
            addToDisposable(
                getOneDebtUseCase.get(Debt(idDebt, BigDecimal("0"), "", "", "", "", "", ""))
                    .subscribeBy(
                        onNext = {
                            amountDebt.set(setMoneyDisplayFormat(it.amount))
                            debtor.set(it.debtor)
                            dateStart.set(it.dateStart)
                            dateFinish.set(it.dateFinish)
                            if (it.type == "fromMe") {
                                isFromMe.set(true)
                                isToMe.set(false)
                            } else {
                                isFromMe.set(false)
                                isToMe.set(true)
                            }
                            if (it.account == "card") {
                                isCard.set(true)
                                isCash.set(false)
                            } else {
                                isCard.set(false)
                                isCash.set(true)
                            }
                            commentDebt.set(it.comment)
                        }
                    )
            )
        }
    }

    //добавление долга
    fun addDebt() {
        val type = when {
            (isFromMe.get()) -> "fromMe"
            else -> "toMe"
        }
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        addToDisposable(
            addDebtUseCase.add(
                DebtAdd(
                    amountDebt.get()!!.toBigDecimal(),
                    debtor.get()!!,
                    dateStart.get()!!,
                    dateFinish.get()!!,
                    type,
                    account,
                    commentDebt.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.debtAddedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //обновление долга
    fun updateDebt() {
        val type = when {
            (isFromMe.get()) -> "fromMe"
            else -> "toMe"
        }
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        addToDisposable(
            updateDebtUseCase.update(
                Debt(
                    idDebt,
                    amountDebt.get()!!.toBigDecimal(),
                    debtor.get()!!,
                    dateStart.get()!!,
                    dateFinish.get()!!,
                    type,
                    account,
                    commentDebt.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.debtUpdatedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //удаление долга
    fun deleteDebt() {
        addToDisposable(
            deleteDebtUseCase.delete(Debt(idDebt, BigDecimal("0"), "", "", "", "", "", ""))
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.debtDeletedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //изменяем дату
    fun dateChange(tag: String, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        when (tag) {
            "start" -> {
                dateStart.set(setTodayDataDisplayFormat(c.time))
            }
            "finish" -> {
                dateFinish.set(setTodayDataDisplayFormat(c.time))
            }
        }
    }
}