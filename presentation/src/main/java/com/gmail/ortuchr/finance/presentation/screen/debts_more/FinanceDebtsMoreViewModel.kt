package com.gmail.ortuchr.finance.presentation.screen.debts_more

import androidx.databinding.ObservableField
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.getAll.GetAllDebtsUseCase
import com.gmail.ortuchr.domain.usecases.getSym.*
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.presentation.adapter.FinanceDebtAdapter
import com.gmail.ortuchr.finance.presentation.base.BaseViewModel
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import com.gmail.ortuchr.finance.presentation.utils.setMoneyDisplayFormat
import com.gmail.ortuchr.finance.presentation.utils.setTodayDataDisplayFormat
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject

class FinanceDebtsMoreViewModel : BaseViewModel<FinanceRouter>() {

    //обсерваблы для подсчета суммы долгов
    val debtsToMe = ObservableField<String>("0.00")
    val debtsFromMe = ObservableField<String>("0.00")
    private val amountCardTotal = ObservableField<String>("0.00")
    private val amountCashTotal = ObservableField<String>("0.00")

    //переменные для передачи даты
    private lateinit var dateOfSetup: String
    private lateinit var dateFrom: String
    private lateinit var dateFromThisMonth: String
    private lateinit var dateTo: String

    @Inject
    lateinit var getAllDebtsUseCase: GetAllDebtsUseCase
    @Inject
    lateinit var getSymDebtsUseCase: GetSymDebtsUseCase
    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase
    @Inject
    lateinit var getSymBalanceCardUseCase: GetSymBalanceCardUseCase
    @Inject
    lateinit var getSymBalanceCardWithDebtsUseCase: GetSymBalanceCardWithDebtsUseCase
    @Inject
    lateinit var getSymBalanceCashUseCase: GetSymBalanceCashUseCase
    @Inject
    lateinit var getSymBalanceCashWithDebtsUseCase: GetSymBalanceCashWithDebtsUseCase

    //устанавливаем адаптер для отображания долгов мне
    val adapterDebtToMe: FinanceDebtAdapter = FinanceDebtAdapter { debt ->
        router?.goToDebtsNew(debt.id, amountCardTotal.get()!!, amountCashTotal.get()!!, debt.amount.toString(), debt.account, debt.type)
    }

    //устанавливаем адаптер для отображания моих долгов
    val adapterDebtFromMe: FinanceDebtAdapter = FinanceDebtAdapter { debt ->
        router?.goToDebtsNew(debt.id, amountCardTotal.get()!!, amountCashTotal.get()!!, debt.amount.toString(), debt.account, debt.type)
    }

    init {
        App.appComponent.inject(this)
    }

    //загружаем данные
    fun setData() {
        debtsToMe.set("0.00")
        debtsFromMe.set("0.00")
        amountCardTotal.set("0.00")
        amountCashTotal.set("0.00")

        //настройка дат поиска
        var calendar = Calendar.getInstance()
        //дата, до которой ведем поиск (сегодня)
        dateTo = setTodayDataDisplayFormat(calendar.time)
        calendar = Calendar.getInstance()
        calendar.set(2000, Calendar.JANUARY, 1)
        //дата, раньше которой точно ничего не будет
        dateOfSetup = setTodayDataDisplayFormat(calendar.time)

        //загружаем долги мне
        addToDisposable(
            getAllDebtsUseCase.getAllDebts("toMe")
                .subscribeBy {
                    adapterDebtToMe.setData(it)
                }
        )
        //загружаем мои долги
        addToDisposable(
            getAllDebtsUseCase.getAllDebts("fromMe")
                .subscribeBy {
                    adapterDebtFromMe.setData(it)
                }
        )
        //загружаем сумму долгов мне
        addToDisposable(
            getSymDebtsUseCase.getSymDebts("toMe", dateOfSetup, dateTo)
                .subscribeBy {
                    debtsToMe.set(setMoneyDisplayFormat(-it))
                }
        )
        //загружаем сумму моих долгов
        addToDisposable(
            getSymDebtsUseCase.getSymDebts("fromMe", dateOfSetup, dateTo)
                .subscribeBy {
                    debtsFromMe.set(setMoneyDisplayFormat(it))
                }
        )

        //загружаем сумму баланса на карте и наличных для передачи в адаптер
        // загружаем настройку: с какого числа месяца считаем
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
                                            if (thirdSetting.value == 1) {
                                                //загружаем бюджет с учетом долгов
                                                addToDisposable(
                                                    getSymBalanceCardWithDebtsUseCase.getSymBalanceCardWithDebts(dateFrom, dateTo)
                                                        .subscribeBy {
                                                            amountCardTotal.set(setMoneyDisplayFormat(amountCardTotal.get()!!.toBigDecimal() + (it)))
                                                        }
                                                )
                                                addToDisposable(
                                                    getSymBalanceCashWithDebtsUseCase.getSymBalanceCashWithDebts(dateFrom, dateTo)
                                                        .subscribeBy {
                                                            amountCashTotal.set(setMoneyDisplayFormat(amountCashTotal.get()!!.toBigDecimal() + (it)))
                                                        }
                                                )
                                            } else {
                                                //загружаем бюджет без учета долгов
                                                addToDisposable(
                                                    getSymBalanceCardUseCase.getSymBalanceCard(dateFrom, dateTo)
                                                        .subscribeBy {
                                                            amountCardTotal.set(setMoneyDisplayFormat(it))
                                                        }
                                                )
                                                addToDisposable(
                                                    getSymBalanceCashUseCase.getSymBalanceCash(dateFrom, dateTo)
                                                        .subscribeBy {
                                                            amountCashTotal.set(setMoneyDisplayFormat(it))
                                                        }
                                                )
                                            }
                                        }
                                )
                            }
                    )
                }
        )
    }
}