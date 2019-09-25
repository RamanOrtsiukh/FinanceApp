package com.gmail.ortuchr.finance.presentation.screen.balance_new

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.entity.BalanceAdd
import com.gmail.ortuchr.domain.usecases.balance.*
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


class FinanceBalanceNewViewModel : BaseViewModel<FinanceRouter>() {

    //обсерваблы для отображения данных
    private lateinit var idIncome: String
    val isNewIncome = ObservableBoolean(true)
    val amountIncome = ObservableField<String>("")
    val dateIncome = ObservableField<String>("")
    val isCard = ObservableBoolean(true)
    val isCash = ObservableBoolean(false)
    val isCardToCash = ObservableBoolean(false)
    val isCashToCard = ObservableBoolean(false)
    val commentIncome = ObservableField<String>("")

    @Inject
    lateinit var getOneBalanceUseCase: GetOneBalanceUseCase
    @Inject
    lateinit var addBalanceUseCase: AddBalanceUseCase
    @Inject
    lateinit var updateBalanceUseCase: UpdateBalanceUseCase
    @Inject
    lateinit var deleteBalanceUseCase: DeleteBalanceUseCase
    @Inject
    lateinit var transferBalanceUseCase: TransferBalanceUseCase

    init {
        App.appComponent.inject(this)
    }

    //заполняем данные если это существующий баланс
    fun isNewIncome(id: String) {
        idIncome = id
        if (idIncome.isEmpty()) {
            isNewIncome.set(true)
            dateIncome.set(setTodayDataDisplayFormat(Date()))
        } else {
            isNewIncome.set(false)
            addToDisposable(
                getOneBalanceUseCase.get(Balance(idIncome, BigDecimal("0"), "", "", "", ""))
                    .subscribeBy(
                        onNext = {
                            amountIncome.set(setMoneyDisplayFormat(it.amount))
                            dateIncome.set(it.date)
                            if (it.account == "card") {
                                isCard.set(true)
                                isCash.set(false)
                            } else {
                                isCard.set(false)
                                isCash.set(true)
                            }
                            commentIncome.set(it.comment)
                        }
                    )
            )
        }
    }

    //добавляем пополнение на карту или в наличные
    fun addIncome() {
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        //добавляем баланс
        addToDisposable(
            addBalanceUseCase.add(
                BalanceAdd(
                    amountIncome.get()!!.toBigDecimal(),
                    dateIncome.get()!!,
                    "income",
                    account,
                    commentIncome.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.incomeAddedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                }
                )
        )
    }

    //добавляем перевод с карты в наличные или с наличных на карту
    fun addTransfer(accountFrom: String, accountTo: String) {
        addToDisposable(
            transferBalanceUseCase.transfer(
                BalanceAdd(
                    -amountIncome.get()!!.toBigDecimal(),
                    dateIncome.get()!!,
                    "transfer",
                    accountFrom,
                    ""
                ),
                BalanceAdd(
                    amountIncome.get()!!.toBigDecimal(),
                    dateIncome.get()!!,
                    "transfer",
                    accountTo,
                    ""
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.transferAddedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                }
                )
        )
    }

    //обновляем существующий баланс
    fun updateIncome() {
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        addToDisposable(
            updateBalanceUseCase.update(
                Balance(
                    idIncome,
                    amountIncome.get()!!.toBigDecimal(),
                    dateIncome.get()!!,
                    "income",
                    account,
                    commentIncome.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.incomeUpdatedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //удаляем существующий баланс
    fun deleteIncome() {
        addToDisposable(
            deleteBalanceUseCase.delete(Balance(idIncome, BigDecimal("0"), "", "", "", ""))
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.incomeDeletedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //изменяем дату баланса
    fun dateChange(year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        dateIncome.set(setTodayDataDisplayFormat(c.time))
    }
}