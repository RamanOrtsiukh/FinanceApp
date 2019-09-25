package com.gmail.ortuchr.finance.presentation.screen.balance_more

import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.data.DeleteDataUseCase
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

class FinanceBalanceMoreViewModel : BaseViewModel<FinanceRouter>() {
    //обсерваблы для записи в вьюмодел
    val balanceAmount = ObservableField<String>("0.00")
    val balanceAmountCard = ObservableField<String>("0.00")
    val balanceAmountCash = ObservableField<String>("0.00")
    val balanceAmountLast = ObservableField<String>("0.00")
    val balanceAmountCardLast = ObservableField<String>("0.00")
    val balanceAmountCashLast = ObservableField<String>("0.00")

    //обсерваблы для записи настроек
    private var settingDayFromValue = 1
    val settingDayFrom = ObservableField<String>()
    val settingWithPastMonths = ObservableBoolean(false)
    val settingWithDebts = ObservableBoolean(false)
    val settingShowHints = ObservableBoolean(false)
    val settingShowWarnings = ObservableBoolean(false)


    //переменные для передачи даты
    private lateinit var dateOfSetup: String
    private lateinit var dateFrom: String
    private lateinit var dateFromThisMonth: String
    private lateinit var dateTo: String
    private lateinit var dateToThisMonth: String

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
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase
    @Inject
    lateinit var deleteDataUseCase: DeleteDataUseCase

    //инициализация даггера
    init {
        App.appComponent.inject(this)
    }

    //подгружаем настройки
    fun showData() {
        //загружаем настройку: с какого числа месяца считаем
        addToDisposable(
            loadSettingUseCase.load(Setting("1", 0))
                .subscribe { firstSetting ->
                    //получаем первую настройку, значение - день с которого считаем
                    settingDayFromValue = firstSetting.value
                    settingDayFrom.set(settingDayFromValue.toString())
                    //загружаем настройку: считать ли предыдущие месяцы в балансе
                    addToDisposable(
                        loadSettingUseCase.load(Setting("2", 0))
                            .subscribe { secondSetting ->
                                //получаем вторую настройку, если значение 1 - поиск от самой дальней даты
                                settingWithPastMonths.set(secondSetting.value == 1)
                                //загружаем настройку: считать ли долги в балансе
                                addToDisposable(
                                    loadSettingUseCase.load(Setting("3", 0))
                                        .subscribe { thirdSetting ->
                                            //получаем третью настройку, если значение 1 - считаем долги в общем бюджете
                                            settingWithDebts.set(thirdSetting.value == 1)
                                            addToDisposable(
                                                loadSettingUseCase.load(Setting("4", 0))
                                                    .subscribe { fourthSetting ->
                                                        //получаем четвертую настройку, если значение 1 - показываем подсказки
                                                        settingShowHints.set(fourthSetting.value == 1)
                                                        addToDisposable(
                                                            loadSettingUseCase.load(Setting("5", 0))
                                                                .subscribe { fifthSetting ->
                                                                    //получаем пятую настройку, если значение 1 - показываем предупреждения
                                                                    settingShowWarnings.set(fifthSetting.value == 1)
                                                                    //отображаем результаты
                                                                    loadDataToShow()
                                                                }
                                                        )
                                                    }
                                            )
                                        }
                                )
                            }
                    )
                }
        )
    }

    //подгружаем и показываем данные
    private fun loadDataToShow() {
        //обнуление переменных, которые могут остаться с предудыщего поиска
        balanceAmount.set("0.00")
        balanceAmountCard.set("0.00")
        balanceAmountCash.set("0.00")
        balanceAmountLast.set("0.00")
        balanceAmountCardLast.set("0.00")
        balanceAmountCashLast.set("0.00")

        //настройка дат поиска
        var calendar = Calendar.getInstance()
        //дата, до которой ведем поиск (сегодня)
        dateTo = setTodayDataDisplayFormat(calendar.time)
        calendar = Calendar.getInstance()
        calendar.set(2000, Calendar.JANUARY, 1)
        //дата, раньше которой точно ничего не будет
        dateOfSetup = setTodayDataDisplayFormat(calendar.time)

        calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, settingDayFromValue)
        if (settingDayFromValue > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            calendar.set(
                Calendar.MONTH,
                Calendar.getInstance().get(Calendar.MONTH) - 1
            )
        //устанавливаем дату от которой считаем в этом месяце
        dateFromThisMonth = setTodayDataDisplayFormat(calendar.time)
        dateFrom = when {
            (settingWithPastMonths.get()) -> dateOfSetup
            else -> dateFromThisMonth
        }
        calendar = Calendar.getInstance()
        if (settingDayFromValue > Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            calendar.set(
                Calendar.MONTH,
                Calendar.getInstance().get(Calendar.MONTH) - 1
            )
        calendar.set(Calendar.DAY_OF_MONTH, settingDayFromValue - 1)
        //устанавливаем дату до которой считаем до текущего месяца
        dateToThisMonth = setTodayDataDisplayFormat(calendar.time)
        if (settingWithDebts.get()) {
            //загружаем бюджет с учетом долгов
            loadBalanceWithDebts()
        } else {
            //загружаем бюджет без учета долгов
            loadBalance()
        }
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
        //загружаем суммарный баланс до текущего месяца
        addToDisposable(
            getSymBalanceUseCase.getSymBalance(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountLast.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс по карте
        addToDisposable(
            getSymBalanceCardUseCase.getSymBalanceCard(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCard.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс по карте до текущего месяца
        addToDisposable(
            getSymBalanceCardUseCase.getSymBalanceCard(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountCardLast.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс наличных
        addToDisposable(
            getSymBalanceCashUseCase.getSymBalanceCash(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCash.set(setMoneyDisplayFormat(it))
                }
        )
        //загружаем баланс наличных до текущего месяца
        addToDisposable(
            getSymBalanceCashUseCase.getSymBalanceCash(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountCashLast.set(setMoneyDisplayFormat(it))
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
        //загружаем суммарный баланс до текущего месяца
        addToDisposable(
            getSymBalanceWithDebtsUseCase.getSymBalanceWithDebts(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountLast.set(setMoneyDisplayFormat(balanceAmountLast.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс по карте
        addToDisposable(
            getSymBalanceCardWithDebtsUseCase.getSymBalanceCardWithDebts(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCard.set(setMoneyDisplayFormat(balanceAmountCard.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс по карте до текущего месяца
        addToDisposable(
            getSymBalanceCardWithDebtsUseCase.getSymBalanceCardWithDebts(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountCardLast.set(setMoneyDisplayFormat(balanceAmountCardLast.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс наличных
        addToDisposable(
            getSymBalanceCashWithDebtsUseCase.getSymBalanceCashWithDebts(dateFrom, dateTo)
                .subscribeBy {
                    balanceAmountCash.set(setMoneyDisplayFormat(balanceAmountCash.get()!!.toBigDecimal() + (it)))
                }
        )
        //загружаем баланс наличных до текущего месяца
        addToDisposable(
            getSymBalanceCashWithDebtsUseCase.getSymBalanceCashWithDebts(dateOfSetup, dateToThisMonth)
                .subscribeBy {
                    balanceAmountCashLast.set(setMoneyDisplayFormat(balanceAmountCashLast.get()!!.toBigDecimal() + (it)))
                }
        )
    }

    //при обновлении настроек - обновляем данные
    private fun refreshData() {
        loadDataToShow()
    }

    //сохраняем первую настройку - день, с которого считаем месяц
    fun saveSettingDayFrom(value: Int) {
        settingDayFromValue = value
        if (settingDayFromValue != settingDayFrom.get()!!.toInt())
        {
            settingDayFrom.set(settingDayFromValue.toString())
            addToDisposable(
                saveSettingUseCase.save(Setting("1", value))
                    .subscribeBy(onComplete = {
                        refreshData()
                    })
            )
        }

    }

    //сохраняем вторую настройку, считаем ли предыдущие месяцы (1- считаем, 0 - не считаем)
    fun saveSettingWithPastMonths() {
        val value = if (settingWithPastMonths.get()) 1 else 0
        addToDisposable(
            saveSettingUseCase.save(Setting("2", value))
                .subscribeBy(onComplete = {
                    refreshData()
                })
        )
    }

    //сохраняем третью настройку, учитываем ли долги в балансе (1 - учитываем, 0 - не учитываем)
    fun saveSettingWithDebts() {
        val value = if (settingWithDebts.get()) 1 else 0
        addToDisposable(
            saveSettingUseCase.save(Setting("3", value))
                .subscribeBy(onComplete = {
                    refreshData()
                })
        )
    }

    //сохраняем четвертую настройку, показываем ли подсказки (1 - показываем, 0 - не показываем)
    fun saveSettingShowHints() {
        val value = if (settingShowHints.get()) 1 else 0
        addToDisposable(
            saveSettingUseCase.save(Setting("4", value))
                .subscribeBy()
        )
    }

    //сохраняем пятую настройку, показываем ли предупреждения (1 - показываем, 0 - не показываем)
    fun saveSettingShowWarnings() {
        val value = if (settingShowWarnings.get()) 1 else 0
        addToDisposable(
            saveSettingUseCase.save(Setting("5", value))
                .subscribeBy()
        )
    }

    //удаляем все данные
    fun deleteData() {
        addToDisposable(
            deleteDataUseCase.delete()
                .subscribeBy(onComplete = {
                    refreshData()
                    Toast.makeText(
                        App.instance.applicationContext,
                        App.instance.getString(R.string.dataDeleteAllTitle),
                        Toast.LENGTH_SHORT
                    ).show()
                })
        )
    }
}