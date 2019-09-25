package com.gmail.ortuchr.finance.presentation.screen.expenses_more

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.gmail.ortuchr.data.local.entity.setDateSaveFormat
import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.category.LoadCategoryUseCase
import com.gmail.ortuchr.domain.usecases.getAll.GetAllBalanceUseCase
import com.gmail.ortuchr.domain.usecases.getSym.*
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.adapter.FinanceBalanceAdapter
import com.gmail.ortuchr.finance.presentation.base.BaseViewModel
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat
import com.gmail.ortuchr.finance.presentation.utils.setPercentsDisplayFormat
import com.gmail.ortuchr.finance.presentation.utils.setTodayDataDisplayFormat
import io.reactivex.rxkotlin.subscribeBy
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FinanceExpensesMoreViewModel : BaseViewModel<FinanceRouter>() {

    val dateFrom = ObservableField<String>("")
    val dateTo = ObservableField<String>("")
    val amountTotal = ObservableField<String>("0.00")
    private val amountCardTotal = ObservableField<String>("0.00")
    private val amountCashTotal = ObservableField<String>("0.00")
    val amountIncome = ObservableField<String>("0.00")
    val amountExpenses = ObservableField<String>("0.00")
    private val amountAllExpenses = ObservableField<String>("0.00")
    val textShowPercents = ObservableField<String>(App.instance.getString(R.string.ShowPercentsTitle))
    //массив для получения процентов по каждому типу
    private val percentsOfTypes = Array<BigDecimal>(14) { BigDecimal.ZERO }

    //обсерваблы для отображения значения каждого типа и его проценты
    val percentsVisibility = ObservableBoolean(false)
    val amountItem1 = ObservableField<String>("0.00")
    val amountItem2 = ObservableField<String>("0.00")
    val amountItem3 = ObservableField<String>("0.00")
    val amountItem4 = ObservableField<String>("0.00")
    val amountItem5 = ObservableField<String>("0.00")
    val amountItem6 = ObservableField<String>("0.00")
    val amountItem7 = ObservableField<String>("0.00")
    val amountItem8 = ObservableField<String>("0.00")
    val amountItem9 = ObservableField<String>("0.00")
    val amountItem10 = ObservableField<String>("0.00")
    val amountItem11 = ObservableField<String>("0.00")
    val amountItem12 = ObservableField<String>("0.00")
    val amountItem13 = ObservableField<String>("0.00")
    val amountItem14 = ObservableField<String>("0.00")
    val percentsItem1 = ObservableField<String>("")
    val percentsItem2 = ObservableField<String>("")
    val percentsItem3 = ObservableField<String>("")
    val percentsItem4 = ObservableField<String>("")
    val percentsItem5 = ObservableField<String>("")
    val percentsItem6 = ObservableField<String>("")
    val percentsItem7 = ObservableField<String>("")
    val percentsItem8 = ObservableField<String>("")
    val percentsItem9 = ObservableField<String>("")
    val percentsItem10 = ObservableField<String>("")
    val percentsItem11 = ObservableField<String>("")
    val percentsItem12 = ObservableField<String>("")
    val percentsItem13 = ObservableField<String>("")
    val percentsItem14 = ObservableField<String>("")
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

    //подгрузка юзкейсов
    @Inject
    lateinit var getSymBalanceFromListUseCase: GetSymBalanceFromListUseCase
    @Inject
    lateinit var getSymBalanceFromListWithDebtsUseCase: GetSymBalanceFromListWithDebtsUseCase
    @Inject
    lateinit var getSymIncomeUseCase: GetSymIncomeUseCase
    @Inject
    lateinit var getSymExpensesUseCase: GetSymExpensesUseCase
    @Inject
    lateinit var getSymExpensesFromListUseCase: GetSymExpensesFromListUseCase
    @Inject
    lateinit var getSymExpensesFromTypeUseCase: GetSymExpensesFromTypeUseCase
    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var getAllBalanceUseCase: GetAllBalanceUseCase
    @Inject
    lateinit var loadCategoryUseCase: LoadCategoryUseCase
    @Inject
    lateinit var getSymBalanceCardUseCase: GetSymBalanceCardUseCase
    @Inject
    lateinit var getSymBalanceCardWithDebtsUseCase: GetSymBalanceCardWithDebtsUseCase
    @Inject
    lateinit var getSymBalanceCashUseCase: GetSymBalanceCashUseCase
    @Inject
    lateinit var getSymBalanceCashWithDebtsUseCase: GetSymBalanceCashWithDebtsUseCase

    //устанавливаем адаптер для ресайкла
    val adapterBalance: FinanceBalanceAdapter = FinanceBalanceAdapter { balance ->
        if (balance.type == "income") {
            router?.goToBalanceNew(
                balance.id,
                amountCardTotal.get()!!,
                amountCashTotal.get()!!,
                balance.amount.toString(),
                balance.account
            )
        } else {
            router?.goToExpensesNew(
                balance.id,
                amountCardTotal.get()!!,
                amountCashTotal.get()!!,
                balance.amount.toString(),
                balance.account
            )
        }
    }

    private val choiceCategoriesArrayList = ArrayList<String>(15)

    init {
        App.appComponent.inject(this)
    }

    //загружаем названия категорий
    fun loadCategoriesTitle() {
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
    private fun setData() {
        //обнуление переменных, которые могут остаться с предудыщего поиска
        amountTotal.set("0.00")
        amountIncome.set("0.00")
        amountExpenses.set("0.00")
        amountAllExpenses.set("0.00")
        amountCardTotal.set("0.00")
        amountCashTotal.set("0.00")

        //настройка дат поиска
        var calendar = Calendar.getInstance()
        //дата, до которой ведем поиск
        if (dateTo.get()!!.isEmpty()) dateTo.set(setTodayDataDisplayFormat(calendar.time))

        //загружаем настройку: с какого числа месяца считаем
        addToDisposable(
            loadSettingUseCase.load(Setting("1", 0))
                .subscribeBy { firstSetting ->
                    //получаем первую настройку, значение - день с которого считаем
                    //загружаем настройку: считать ли долги в балансе
                    addToDisposable(
                        loadSettingUseCase.load(Setting("3", 0))
                            .subscribeBy { thirdSetting ->
                                //получаем третью настройку, если значение 1 - считаем долги в общем бюджете
                                calendar = Calendar.getInstance()
                                calendar.set(Calendar.DAY_OF_MONTH, firstSetting.value)
                                if (firstSetting.value > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
                                    calendar.set(
                                        Calendar.MONTH,
                                        Calendar.getInstance().get(Calendar.MONTH) - 1
                                    )
                                if (dateFrom.get()!!.isEmpty()) dateFrom.set(setTodayDataDisplayFormat(calendar.time))
                                //загружам доходы и расходы
                                loadIncomesAndExpenses()
                                if (thirdSetting.value == 1) {
                                    //загружаем бюджет с учетом долгов
                                    loadBalanceWithDebts()
                                } else {
                                    //загружаем бюджет без учета долгов
                                    loadBalance()
                                }
                                //загружаем все доходы и расходы
                                addToDisposable(
                                    getAllBalanceUseCase.getAllBalance(
                                        dateFrom.get()!!,
                                        dateTo.get()!!,
                                        choiceCategoriesArrayList.toList()
                                    )
                                        .subscribeBy {
                                            adapterBalance.setData(it)
                                        }
                                )
                                //загружаем расходы по категориям
                                loadSeparateExpense()
                            }
                    )
                }
        )
    }

    //загружаем баланс без учета долгов
    private fun loadBalance() {
        //загружаем суммарный баланс
        addToDisposable(
            getSymBalanceFromListUseCase.getSymBalanceFromList(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceCategoriesArrayList.toList()
            )
                .subscribeBy {
                    amountTotal.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс по карте и наличным для передачи в адаптер
        addToDisposable(
            getSymBalanceCardUseCase.getSymBalanceCard(dateFrom.get()!!, dateTo.get()!!)
                .subscribeBy {
                    amountCardTotal.set(setMoneyDisplayFormat(it))
                }
        )
        addToDisposable(
            getSymBalanceCashUseCase.getSymBalanceCash(dateFrom.get()!!, dateTo.get()!!)
                .subscribeBy {
                    amountCashTotal.set(setMoneyDisplayFormat(it))
                }
        )
    }

    //загружаем баланс с учетом долгов
    private fun loadBalanceWithDebts() {
        //загружаем суммарный баланс
        addToDisposable(
            getSymBalanceFromListWithDebtsUseCase.getSymBalanceFromListWithDebts(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceCategoriesArrayList.toList()
            )
                .subscribeBy {
                    amountTotal.set(setMoneyDisplayFormat(amountTotal.get()!!.toBigDecimal() + it))
                }
        )
        //загружаем баланс по карте и наличным для передачи в адаптер
        addToDisposable(
            getSymBalanceCardWithDebtsUseCase.getSymBalanceCardWithDebts(dateFrom.get()!!, dateTo.get()!!)
                .subscribeBy {
                    amountCardTotal.set(setMoneyDisplayFormat(amountCardTotal.get()!!.toBigDecimal() + (it)))
                }
        )
        addToDisposable(
            getSymBalanceCashWithDebtsUseCase.getSymBalanceCashWithDebts(dateFrom.get()!!, dateTo.get()!!)
                .subscribeBy {
                    amountCashTotal.set(setMoneyDisplayFormat(amountCashTotal.get()!!.toBigDecimal() + (it)))
                }
        )
    }

    //загружаем доходы и расходы
    private fun loadIncomesAndExpenses() {
        //загружаем доходы
        if (choiceCategoriesArrayList.contains("income")) {
            addToDisposable(
                getSymIncomeUseCase.getSymIncome(dateFrom.get()!!, dateTo.get()!!)
                    .subscribeBy {
                        amountIncome.set(setMoneyDisplayFormat(it))
                    }
            )
        } else {
            amountIncome.set("0.00")
        }
        //загружаем расходы из списка
        addToDisposable(
            getSymExpensesFromListUseCase.getSymExpensesFromList(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceCategoriesArrayList.toList()
            )
                .subscribeBy {
                    amountExpenses.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем общие расходы
        addToDisposable(
            getSymExpensesUseCase.getSymExpenses(dateFrom.get()!!, dateTo.get()!!)
                .subscribeBy {
                    amountAllExpenses.set(setMoneyDisplayFormat(it))
                }
        )
    }

    //загружаем расходы по категориям
    private fun loadSeparateExpense() {
        amountItem1.set("0.00")
        amountItem2.set("0.00")
        amountItem3.set("0.00")
        amountItem4.set("0.00")
        amountItem5.set("0.00")
        amountItem6.set("0.00")
        amountItem7.set("0.00")
        amountItem8.set("0.00")
        amountItem9.set("0.00")
        amountItem10.set("0.00")
        amountItem11.set("0.00")
        amountItem12.set("0.00")
        amountItem13.set("0.00")
        amountItem14.set("0.00")
        Arrays.fill(percentsOfTypes, BigDecimal("0.00"))

        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle1.get()!!
            )
                .subscribeBy {
                    amountItem1.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[0] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle2.get()!!
            )
                .subscribeBy {
                    amountItem2.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[1] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle3.get()!!
            )
                .subscribeBy {
                    amountItem3.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[2] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle4.get()!!
            )
                .subscribeBy {
                    amountItem4.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[3] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle5.get()!!
            )
                .subscribeBy {
                    amountItem5.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[4] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle6.get()!!
            )
                .subscribeBy {
                    amountItem6.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[5] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle7.get()!!
            )
                .subscribeBy {
                    amountItem7.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[6] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle8.get()!!
            )
                .subscribeBy {
                    amountItem8.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[7] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle9.get()!!
            )
                .subscribeBy {
                    amountItem9.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[8] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle10.get()!!
            )
                .subscribeBy {
                    amountItem10.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[9] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle11.get()!!
            )
                .subscribeBy {
                    amountItem11.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[10] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle12.get()!!
            )
                .subscribeBy {
                    amountItem12.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[11] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle13.get()!!
            )
                .subscribeBy {
                    amountItem13.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[12] = -it
                }
        )
        addToDisposable(
            getSymExpensesFromTypeUseCase.getSymExpensesFromType(
                dateFrom.get()!!,
                dateTo.get()!!,
                choiceTitle14.get()!!
            )
                .subscribeBy {
                    amountItem14.set(setMoneyDisplayFormat(-it))
                    percentsOfTypes[13] = -it
                }
        )
    }

    //отображение процентов
    fun showPercents() {
        //показываем или скрываем проценты
        if (percentsVisibility.get()) {
            textShowPercents.set(App.instance.getString(R.string.ShowPercentsTitle))
            percentsVisibility.set(false)
        } else {
            textShowPercents.set(App.instance.getString(R.string.ShowListTitle))
            setPercents()
            percentsVisibility.set(true)
        }
    }

    //устанавливаем проценты
    private fun setPercents() {
        //устанавливаем процент каждого типа с помощью алгоритма
        //сначала собираем сумму процентов включая текущий элемент
        //округляем, чтобы получить в сумме 100%
        var symPercents = BigDecimal("0")
        val symPercentsArray = Array<BigDecimal>(14) { BigDecimal.ZERO }
        for ((index, value) in percentsOfTypes.withIndex()) {
            if (amountAllExpenses.get()!!.toBigDecimal() != BigDecimal("0.00")) {
                symPercents = symPercents.add(
                    value
                        .divide(
                            -amountAllExpenses.get()!!.toBigDecimal(),
                            5,
                            BigDecimal.ROUND_HALF_UP
                        )
                        .multiply(BigDecimal("100"))
                )
            }
            symPercentsArray[index] = symPercents.setScale(1, BigDecimal.ROUND_HALF_UP)
            if (symPercents > BigDecimal("100")) {
                symPercentsArray[index] = BigDecimal("100")
                symPercents = BigDecimal("100")
            }
        }
        //затем для каждого типа отнимаем сумму предыдущих процентов
        //чтобы получить округленный результат и 100% в сумме
        symPercents = BigDecimal("0")
        for ((index, value) in symPercentsArray.withIndex()) {
            symPercentsArray[index] = symPercentsArray[index].subtract(symPercents)
            symPercents = value
            if (symPercentsArray[index] < BigDecimal("0")) {
                symPercentsArray[index] = BigDecimal("0")
            }
        }

        //устанавливаем полученные проценты в обсерваблы
        percentsItem1.set(setPercentsDisplayFormat(symPercentsArray[0]))
        percentsItem2.set(setPercentsDisplayFormat(symPercentsArray[1]))
        percentsItem3.set(setPercentsDisplayFormat(symPercentsArray[2]))
        percentsItem4.set(setPercentsDisplayFormat(symPercentsArray[3]))
        percentsItem5.set(setPercentsDisplayFormat(symPercentsArray[4]))
        percentsItem6.set(setPercentsDisplayFormat(symPercentsArray[5]))
        percentsItem7.set(setPercentsDisplayFormat(symPercentsArray[6]))
        percentsItem8.set(setPercentsDisplayFormat(symPercentsArray[7]))
        percentsItem9.set(setPercentsDisplayFormat(symPercentsArray[8]))
        percentsItem10.set(setPercentsDisplayFormat(symPercentsArray[9]))
        percentsItem11.set(setPercentsDisplayFormat(symPercentsArray[10]))
        percentsItem12.set(setPercentsDisplayFormat(symPercentsArray[11]))
        percentsItem13.set(setPercentsDisplayFormat(symPercentsArray[12]))
        percentsItem14.set(setPercentsDisplayFormat(symPercentsArray[13]))
    }

    //устанавливаем для отслеживания только выбранные категории
    fun setCategories(arrayList: ArrayList<String>) {
        choiceCategoriesArrayList.clear()
        choiceCategoriesArrayList.addAll(arrayList)
        setData()
    }

    //изменяем дату
    fun dateChange(tag: String, year: Int, month: Int, day: Int) {
        val c = Calendar.getInstance()
        c.set(Calendar.YEAR, year)
        c.set(Calendar.MONTH, month)
        c.set(Calendar.DAY_OF_MONTH, day)
        when (tag) {
            "from" -> {
                dateFrom.set(setTodayDataDisplayFormat(c.time))
                if (setDateSaveFormat(dateFrom.get()!!) > setDateSaveFormat(dateTo.get()!!))
                    dateTo.set(dateFrom.get())
            }
            "to" -> {
                dateTo.set(setTodayDataDisplayFormat(c.time))
                if (setDateSaveFormat(dateFrom.get()!!) > setDateSaveFormat(dateTo.get()!!))
                    dateFrom.set(dateTo.get())
            }
        }
        setData()
        textShowPercents.set(App.instance.getString(R.string.ShowPercentsTitle))
        percentsVisibility.set(false)
    }
}