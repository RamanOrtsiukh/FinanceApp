package com.gmail.ortuchr.finance.presentation.screen.main

import androidx.databinding.ObservableField
import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.category.SaveCategoryUseCase
import com.gmail.ortuchr.domain.usecases.getSym.*
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.base.BaseViewModel
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat
import com.gmail.ortuchr.finance.presentation.utils.setTodayDataDisplayFormat
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject

class FinanceMainViewModel : BaseViewModel<FinanceRouter>() {

    //обсерваблы для записи в вьюмодел
    val balanceAmount = ObservableField<String>("0.00")
    val balanceAmountCard = ObservableField<String>("0.00")
    val balanceAmountCash = ObservableField<String>("0.00")
    val expensesAmount = ObservableField<String>("0.00")
    val expensesAmountCard = ObservableField<String>("0.00")
    val expensesAmountCash = ObservableField<String>("0.00")
    val debtsAmount = ObservableField<String>("0.00")

    //переменные для передачи даты
    private lateinit var dateOfSetup: String
    private lateinit var dateFrom: String
    private lateinit var dateFromThisMonth: String
    private lateinit var dateTo: String

    //подгрузка юзкейсов
    @Inject
    lateinit var getSymBalanceUseCase: GetSymBalanceUseCase
    @Inject
    lateinit var getSymBalanceWithDebtsUseCase: GetSymBalanceWithDebtsUseCase
    @Inject
    lateinit var getSymBalanceCardUseCase: GetSymBalanceCardUseCase
    @Inject
    lateinit var getSymBalanceCardWithDebtsUseCase: GetSymBalanceCardWithDebtsUseCase
    @Inject
    lateinit var getSymBalanceCashUseCase: GetSymBalanceCashUseCase
    @Inject
    lateinit var getSymBalanceCashWithDebtsUseCase: GetSymBalanceCashWithDebtsUseCase
    @Inject
    lateinit var getSymExpensesUseCase: GetSymExpensesUseCase
    @Inject
    lateinit var getSymExpensesCardUseCase: GetSymExpensesCardUseCase
    @Inject
    lateinit var getSymExpensesCashUseCase: GetSymExpensesCashUseCase
    @Inject
    lateinit var getSymDebtsUseCase: GetSymDebtsUseCase
    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase
    @Inject
    lateinit var saveCategoryUseCase: SaveCategoryUseCase

    //инициализация даггера
    init {
        App.appComponent.inject(this)
    }

    //переход к подробному балансу
    fun goToBalanceMore() {
        router?.goToBalanceMore()
    }

    //переход к добавлению баланса
    fun goToBalanceNew() {
        router?.goToBalanceNew("", balanceAmountCard.get()!!, balanceAmountCash.get()!!, "0.00", "")
    }

    //переход ко всем долгам
    fun goToDebtsMore() {
        router?.goToDebtsMore()
    }

    //переход к созданию долга
    fun goToDebtsNew() {
        router?.goToDebtsNew("", balanceAmountCard.get()!!, balanceAmountCash.get()!!, "0.00", "", "")
    }

    //переход ко всем расходам
    fun goToExpensesMore() {
        router?.goToExpensesMore()
    }

    //переход к созданию расхода
    fun goToExpensesNew() {
        router?.goToExpensesNew("", balanceAmountCard.get()!!, balanceAmountCash.get()!!, "0.00", "")
    }


    //для первого запуска программы, установка настроек по умолчанию
    fun firstRun() {
        //первый день месяца, с которого считать
        addToDisposable(
            saveSettingUseCase.save(Setting("1", 1))
                .subscribeBy()
        )
        //считать ли предыдущие месяцы (0 - нет, 1 - да)
        addToDisposable(
            saveSettingUseCase.save(Setting("2", 1))
                .subscribeBy()
        )
        //считать ли долги в общем балансе (0 - нет, 1 - да)
        addToDisposable(
            saveSettingUseCase.save(Setting("3", 1))
                .subscribeBy()
        )
        //показываем ли подсказки (0 - нет, 1 - да)
        addToDisposable(
            saveSettingUseCase.save(Setting("4", 1))
                .subscribeBy()
        )
        //показываем ли предупреждения (0 - нет, 1 - да)
        addToDisposable(
            saveSettingUseCase.save(Setting("5", 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("15", App.instance.getString(R.string.incomeTitle), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("1", App.instance.getString(R.string.choice1Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("2", App.instance.getString(R.string.choice2Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("3", App.instance.getString(R.string.choice3Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("4", App.instance.getString(R.string.choice4Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("5", App.instance.getString(R.string.choice5Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("6", App.instance.getString(R.string.choice6Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("7", App.instance.getString(R.string.choice7Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("8", App.instance.getString(R.string.choice8Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("9", App.instance.getString(R.string.choice9Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("10", App.instance.getString(R.string.choice10Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("11", App.instance.getString(R.string.choice11Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("12", App.instance.getString(R.string.choice12Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("13", App.instance.getString(R.string.choice13Title), 1))
                .subscribeBy()
        )
        addToDisposable(
            saveCategoryUseCase.save(Category("14", App.instance.getString(R.string.choice14Title), 1))
                .subscribeBy()
        )
    }


    //отображение данных
    fun showData() {
        //обнуление переменных, которые могут остаться с предудыщего поиска
        balanceAmount.set("0.00")
        balanceAmountCard.set("0.00")
        balanceAmountCash.set("0.00")
        expensesAmount.set("0.00")
        expensesAmountCard.set("0.00")
        expensesAmountCash.set("0.00")
        debtsAmount.set("0.00")

        //настройка дат поиска
        var calendar = Calendar.getInstance()
        //дата, до которой ведем поиск
        dateTo = setTodayDataDisplayFormat(calendar.time)
        calendar = Calendar.getInstance()
        calendar.set(2000, Calendar.JANUARY, 1)
        //дата, раньше которой точно ничего не будет
        dateOfSetup = setTodayDataDisplayFormat(calendar.time)

        //загружаем настройку: с какого числа месяца считаем
        addToDisposable(
            loadSettingUseCase.load(Setting("1", 0))
                .subscribeBy { firstSetting ->
                    //получаем первую настройку, значение - день с которого считаем
                    //загружаем настройку: считать ли предыдущие месяцы в балансе
                    addToDisposable(
                        loadSettingUseCase.load(Setting("2", 0))
                            .subscribeBy { secondSetting ->
                                //получаем вторую настройку, если значение 1 - поиск от самой дальней даты
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
                                            //устанавливаем дату от которой считаем в этом месяце
                                            dateFromThisMonth = setTodayDataDisplayFormat(calendar.time)
                                            dateFrom = when {
                                                (secondSetting.value == 1) -> dateOfSetup
                                                else -> dateFromThisMonth
                                            }
                                            //загружам расходы
                                            loadExpenses()
                                            //загружаем долги
                                            loadDebts()
                                            if (thirdSetting.value == 1) {
                                                //загружаем бюджет с учетом долгов
                                                loadBalanceWithDebts()
                                            } else {
                                                //загружаем бюджет без учета долгов
                                                loadBalance()
                                            }
                                        }
                                )
                            }
                    )
                }
        )
    }

    //загружаем баланс
    private fun loadBalance() {
        //загружаем суммарный баланс
        addToDisposable(
            getSymBalanceUseCase.getSymBalance(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmount.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс по карте
        addToDisposable(
            getSymBalanceCardUseCase.getSymBalanceCard(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCard.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс наличных
        addToDisposable(
            getSymBalanceCashUseCase.getSymBalanceCash(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCash.set(setMoneyDisplayFormat(it))
                }
        )
    }

    //загружаем баланс с учетом долгов
    private fun loadBalanceWithDebts() {
        //загружаем суммарный баланс
        addToDisposable(
            getSymBalanceWithDebtsUseCase.getSymBalanceWithDebts(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmount.set(setMoneyDisplayFormat(balanceAmount.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс по карте
        addToDisposable(
            getSymBalanceCardWithDebtsUseCase.getSymBalanceCardWithDebts(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCard.set(setMoneyDisplayFormat(balanceAmountCard.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс наличных
        addToDisposable(
            getSymBalanceCashWithDebtsUseCase.getSymBalanceCashWithDebts(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCash.set(setMoneyDisplayFormat(balanceAmountCash.get()!!.toBigDecimal() + (it)))
                }
        )
    }

    //загружаем расходы
    private fun loadExpenses() {
        //загружаем общие расходы
        addToDisposable(
            getSymExpensesUseCase.getSymExpenses(dateFromThisMonth, dateTo)
                .subscribeBy {
                    expensesAmount.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем расходы по карте
        addToDisposable(
            getSymExpensesCardUseCase.getSymExpensesCard(dateFromThisMonth, dateTo)
                .subscribeBy {
                    expensesAmountCard.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем расходы наличными
        addToDisposable(
            getSymExpensesCashUseCase.getSymExpensesCash(dateFromThisMonth, dateTo)
                .subscribeBy {
                    expensesAmountCash.set(setMoneyDisplayFormat(it))
                }
        )
    }

    //загружаем долги
    private fun loadDebts() {
        //загружаем общую сумму долгов
        addToDisposable(
            getSymDebtsUseCase.getSymDebts("", dateOfSetup, dateTo)
                .subscribeBy {
                    debtsAmount.set(setMoneyDisplayFormat(-it))
                }
        )
    }
}