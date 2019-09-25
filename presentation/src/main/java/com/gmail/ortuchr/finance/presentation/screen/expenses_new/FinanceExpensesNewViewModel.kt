package com.gmail.ortuchr.finance.presentation.screen.expenses_new

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.entity.BalanceAdd
import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.usecases.balance.AddBalanceUseCase
import com.gmail.ortuchr.domain.usecases.balance.DeleteBalanceUseCase
import com.gmail.ortuchr.domain.usecases.balance.GetOneBalanceUseCase
import com.gmail.ortuchr.domain.usecases.balance.UpdateBalanceUseCase
import com.gmail.ortuchr.domain.usecases.category.LoadCategoryUseCase
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

class FinanceExpensesNewViewModel : BaseViewModel<FinanceRouter>() {

    //переменная для хранения ид существующего расхода
    private lateinit var idExpense: String
    //обсервабл для отображения новая ли это трата
    val isNewExpense = ObservableBoolean(true)
    //обсерваблы для записи данных
    val amountExpense = ObservableField<String>("")
    val dateExpense = ObservableField<String>("")
    private lateinit var typeExpense: String
    val isCard = ObservableBoolean(true)
    val isCash = ObservableBoolean(false)
    val commentExpense = ObservableField<String>("")
    //обсерваблы для хранения названий категорий
    val choiceTitle1 = ObservableField<String>("")
    val choiceTitle2 = ObservableField<String>("")
    val choiceTitle3 = ObservableField<String>("")
    val choiceTitle4 = ObservableField<String>("")
    val choiceTitle5 = ObservableField<String>("")
    val choiceTitle6 = ObservableField<String>("")
    val choiceTitle7 = ObservableField<String>("")
    val choiceTitle8 = ObservableField<String>("")
    val choiceTitle9 = ObservableField<String>("")
    val choiceTitle10 = ObservableField<String>("")
    val choiceTitle11 = ObservableField<String>("")
    val choiceTitle12 = ObservableField<String>("")
    val choiceTitle13 = ObservableField<String>("")
    val choiceTitle14 = ObservableField<String>("")

    //обсервабл для выбранного ид левого радиогрупп
    val selectedItemIdLeft = ObservableInt(0)
    //обсервабл для выбранного ид правого радиогрупп
    val selectedItemIdRight = ObservableInt(0)

    @Inject
    lateinit var getOneBalanceUseCase: GetOneBalanceUseCase
    @Inject
    lateinit var addBalanceUseCase: AddBalanceUseCase
    @Inject
    lateinit var updateBalanceUseCase: UpdateBalanceUseCase
    @Inject
    lateinit var deleteBalanceUseCase: DeleteBalanceUseCase
    @Inject
    lateinit var loadCategoryUseCase: LoadCategoryUseCase

    init {
        App.appComponent.inject(this)
    }

    //загружаем названия категорий
    fun loadCategories() {
        addToDisposable(
            loadCategoryUseCase.load(Category("1", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle1.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("2", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle2.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("3", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle3.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("4", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle4.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("5", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle5.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("6", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle6.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("7", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle7.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("8", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle8.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("9", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle9.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("10", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle10.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("11", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle11.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("12", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle12.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("13", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle13.set(it.title)
                })
        )
        addToDisposable(
            loadCategoryUseCase.load(Category("14", "", 1))
                .subscribeBy(onNext = {
                    choiceTitle14.set(it.title)
                })
        )
    }

    //загружаем данные
    fun isNewExpense(id: String) {
        loadCategories()

        idExpense = id
        //если это новый расход устанавливаем поля по умолчанию
        if (idExpense.isEmpty()) {
            isNewExpense.set(true)
            dateExpense.set(setTodayDataDisplayFormat(Date()))
            isCard.set(true)
            isCash.set(false)
            selectedItemIdLeft.set(R.id.radio1)
        } else {
            //если существующий расход - загружаем данные
            isNewExpense.set(false)
            addToDisposable(
                getOneBalanceUseCase.get(Balance(idExpense, BigDecimal("0"), "", "", "", ""))
                    .subscribeBy(
                        onNext = {
                            amountExpense.set(setMoneyDisplayFormat(it.amount))
                            dateExpense.set(it.date)
                            when (it.type) {
                                choiceTitle1.get() -> selectedItemIdLeft.set(R.id.radio1)
                                choiceTitle2.get() -> selectedItemIdLeft.set(R.id.radio2)
                                choiceTitle3.get() -> selectedItemIdLeft.set(R.id.radio3)
                                choiceTitle4.get() -> selectedItemIdLeft.set(R.id.radio4)
                                choiceTitle5.get() -> selectedItemIdLeft.set(R.id.radio5)
                                choiceTitle6.get() -> selectedItemIdLeft.set(R.id.radio6)
                                choiceTitle7.get() -> selectedItemIdLeft.set(R.id.radio7)
                                choiceTitle8.get() -> selectedItemIdRight.set(R.id.radio8)
                                choiceTitle9.get() -> selectedItemIdRight.set(R.id.radio9)
                                choiceTitle10.get() -> selectedItemIdRight.set(R.id.radio10)
                                choiceTitle11.get() -> selectedItemIdRight.set(R.id.radio11)
                                choiceTitle12.get() -> selectedItemIdRight.set(R.id.radio12)
                                choiceTitle13.get() -> selectedItemIdRight.set(R.id.radio13)
                                choiceTitle14.get() -> selectedItemIdRight.set(R.id.radio14)
                            }
                            if (it.account == "card") {
                                isCard.set(true)
                                isCash.set(false)
                            } else {
                                isCash.set(true)
                                isCard.set(false)
                            }
                            commentExpense.set(it.comment)
                            typeExpense = it.type
                        }
                    )
            )
        }
    }

    //добавление расхода
    fun addExpense() {
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        setType()
        addToDisposable(
            addBalanceUseCase.add(
                BalanceAdd(
                    amountExpense.get()!!.toBigDecimal(),
                    dateExpense.get()!!,
                    typeExpense,
                    account,
                    commentExpense.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.expenseAddedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    amountExpense.set("")
                    commentExpense.set("")
                    resetRightRadioGroup()
                    selectedItemIdLeft.set(R.id.radio1)
                    isCard.set(true)
                }
                )
        )
    }

    //обновление расхода
    fun updateExpense() {
        val account = when {
            (isCard.get()) -> "card"
            else -> "cash"
        }
        setType()
        addToDisposable(
            updateBalanceUseCase.update(
                Balance(
                    idExpense,
                    amountExpense.get()!!.toBigDecimal(),
                    dateExpense.get()!!,
                    typeExpense,
                    account,
                    commentExpense.get()!!
                )
            )
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.expenseUpdatedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //удаление расхода
    fun deleteExpense() {
        addToDisposable(
            deleteBalanceUseCase.delete(Balance(idExpense, BigDecimal("0"), "", "", "", ""))
                .subscribeBy(onComplete = {
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.expenseDeletedTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                    router?.goBack()
                })
        )
    }

    //изменение даты расхода
    fun dateChange(year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        dateExpense.set(setTodayDataDisplayFormat(c.time))
    }

    //выбор типа расхода
    private fun setType() {
        if (selectedItemIdLeft.get() != 0) {
            typeExpense = when (selectedItemIdLeft.get()) {
                R.id.radio1 -> choiceTitle1.get()!!
                R.id.radio2 -> choiceTitle2.get()!!
                R.id.radio3 -> choiceTitle3.get()!!
                R.id.radio4 -> choiceTitle4.get()!!
                R.id.radio5 -> choiceTitle5.get()!!
                R.id.radio6 -> choiceTitle6.get()!!
                R.id.radio7 -> choiceTitle7.get()!!
                else -> choiceTitle1.get()!!
            }
        } else {
            typeExpense = when (selectedItemIdRight.get()) {
                R.id.radio8 -> choiceTitle8.get()!!
                R.id.radio9 -> choiceTitle9.get()!!
                R.id.radio10 -> choiceTitle10.get()!!
                R.id.radio11 -> choiceTitle11.get()!!
                R.id.radio12 -> choiceTitle12.get()!!
                R.id.radio13 -> choiceTitle13.get()!!
                R.id.radio14 -> choiceTitle14.get()!!
                else -> choiceTitle8.get()!!
            }
        }
    }

    //обнуление левой радиогруппы
    fun resetLeftRadioGroup() {
        selectedItemIdLeft.set(0)
    }

    //обнуление правой радиогруппы
    fun resetRightRadioGroup() {
        selectedItemIdRight.set(0)
    }
}