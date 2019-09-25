package com.gmail.ortuchr.finance.presentation.screen.debts_new

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.databinding.FragmentFinanceDebtsNewBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class FinanceDebtsNewFragment : BaseMvvmFragment<
        FinanceDebtsNewViewModel,
        FinanceRouter,
        FragmentFinanceDebtsNewBinding>() {

    companion object {
        private const val ID_EXTRA = "ID_EXTRA"
        private const val SYM_CARD_EXTRA = "SYM_CARD_EXTRA"
        private const val SYM_CASH_EXTRA = "SYM_CASH_EXTRA"
        private const val EXIST_AMOUNT_EXTRA = "EXIST_AMOUNT_EXTRA"
        private const val EXIST_ACCOUNT_EXTRA = "EXIST_ACCOUNT_EXTRA"
        private const val EXIST_TYPE_EXTRA = "EXIST_DEBTOR_EXTRA"

        fun getInstance(
            id: String,
            symCard: String,
            symCash: String,
            existAmount: String,
            existAccount: String,
            existType: String
        ): FinanceDebtsNewFragment {
            val fragment = FinanceDebtsNewFragment()
            val bundle = Bundle()
            bundle.putString(ID_EXTRA, id)
            bundle.putString(SYM_CARD_EXTRA, symCard)
            bundle.putString(SYM_CASH_EXTRA, symCash)
            bundle.putString(EXIST_AMOUNT_EXTRA, existAmount)
            bundle.putString(EXIST_ACCOUNT_EXTRA, existAccount)
            bundle.putString(EXIST_TYPE_EXTRA, existType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var countDialogShow = 0

    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase

    init {
        App.appComponent.inject(this)
    }

    override fun provideViewModel(): FinanceDebtsNewViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceDebtsNewViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_debts_new

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загружаем аргументы
        val id = arguments?.getString(ID_EXTRA, "")
        val symCard = BigDecimal(arguments?.getString(SYM_CARD_EXTRA, "0.00"))
        val symCash = BigDecimal(arguments?.getString(SYM_CASH_EXTRA, "0.00"))
        val existAmount = BigDecimal(arguments?.getString(EXIST_AMOUNT_EXTRA, "0.00"))
        val existAccount = arguments?.getString(EXIST_ACCOUNT_EXTRA, "card")
        val existType = arguments?.getString(EXIST_TYPE_EXTRA, "fromMe")
        viewModel.isNewDebt(id!!)

        //загружаем настройку, если да - показываем подсказку
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintDebtsNewTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintDebtsNewMessage)
                        val checkBox = dialog.findViewById<CheckBox>(R.id.hintsCheck)
                        val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
                        positiveButton.setOnClickListener {
                            if (checkBox.isChecked) {
                                addToDisposable(
                                    saveSettingUseCase.save(Setting("4", 0))
                                        .subscribeBy()
                                )
                            }
                            dialog.dismiss()
                        }
                        dialog.show()
                    }
                }
        )

        //устанавливаем листенер на дату начала долга для ее изменения
        binding.debtDateStart.setOnClickListener {

            class DatePickerFragment : androidx.fragment.app.DialogFragment(), DatePickerDialog.OnDateSetListener {

                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val dialog = DatePickerDialog(it.context, this, year, month, day)
                    dialog.datePicker.maxDate = c.timeInMillis
                    c.set(2000, Calendar.JANUARY, 1)
                    dialog.datePicker.minDate = c.timeInMillis
                    return dialog
                }

                //передаем полученную дату в вьюмодел
                override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.dateChange("start", year, month, day)

                    val c = Calendar.getInstance()
                    c.set(year, month, day)
                    val dateStart = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH).format(c.time)
                    val dateReturn = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH).format(
                        SimpleDateFormat(
                            "dd.MM.yy",
                            Locale.ENGLISH
                        ).parse(binding.debtDateReturn.text.toString())
                    )
                    //если дата начала превышает дату возврата выводим предупреждение
                    if (dateStart > dateReturn) {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.warningErrorDateDebt))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                        viewModel.dateChange("finish", year, month, day)
                    }
                }
            }

            //запускаем диалог
            val newFragment = DatePickerFragment()
            newFragment.isCancelable = false
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        //устанавливаем листенер на дату возвращения долга для ее изменения
        binding.debtDateReturn.setOnClickListener {

            class DatePickerFragment : androidx.fragment.app.DialogFragment(), DatePickerDialog.OnDateSetListener {

                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val dialog = DatePickerDialog(it.context, this, year, month, day)
                    val dateStart =
                        SimpleDateFormat("dd.MM.yy", Locale.ENGLISH).parse(binding.debtDateStart.text.toString())
                    dialog.datePicker.minDate = dateStart.time
                    return dialog
                }

                //передаем полученную дату в вьюмодел
                override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.dateChange("finish", year, month, day)
                }
            }

            //запускаем диалог
            val newFragment = DatePickerFragment()
            newFragment.isCancelable = false
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        //устанавливаем листенер на добавление, для показа предупреждения минусового баланса
        binding.buttonAdd.setOnClickListener {
            if (binding.debtAmount.text.isNullOrEmpty() || binding.debtWho.text.isNullOrEmpty() || BigDecimal(binding.debtAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )
            ) {
                when (binding.debtAmount.text.isNullOrEmpty() || BigDecimal(binding.debtAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )) {
                    true -> {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                    else -> if (binding.radioDebtFromMe.isChecked) {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountDebtFromMeNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    } else {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountDebtToMeNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                }
            } else {
                if (binding.radioDebtFromMe.isChecked) {
                    viewModel.addDebt()
                } else {
                    addToDisposable(
                        loadSettingUseCase.load(Setting("3", 0))
                            .subscribeBy { thirdSetting ->
                                if (thirdSetting.value == 1) {
                                    showDialogOrRun(
                                        binding.radioCardType.isChecked && symCard >= BigDecimal(binding.debtAmount.text.toString())
                                                || binding.radioCashType.isChecked && symCash >= BigDecimal(binding.debtAmount.text.toString()),
                                        view,
                                        "add"
                                    )
                                } else {
                                    viewModel.addDebt()
                                }
                            }
                    )
                }
            }
        }

        //устанавливаем листенер на изменение, для показа предупреждения минусового баланса
        binding.buttonChange.setOnClickListener {
            if (binding.debtAmount.text.isNullOrEmpty() || binding.debtWho.text.isNullOrEmpty() || BigDecimal(binding.debtAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )
            ) {
                when (binding.debtAmount.text.isNullOrEmpty() || BigDecimal(binding.debtAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )) {
                    true -> {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                    else -> if (binding.radioDebtFromMe.isChecked) {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountDebtFromMeNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    } else {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.amountDebtToMeNotFoundTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                }
            } else {
                if (binding.radioDebtFromMe.isChecked) {
                    viewModel.updateDebt()
                } else {
                    addToDisposable(
                        loadSettingUseCase.load(Setting("3", 0))
                            .subscribeBy { thirdSetting ->
                                if (thirdSetting.value == 1) {
                                    if (existAccount == "card") {
                                        if (binding.radioCardType.isChecked) {
                                            showDialogOrRun(
                                                symCard.add(existAmount) >= BigDecimal(binding.debtAmount.text.toString()),
                                                it,
                                                "update"
                                            )
                                        } else {
                                            showDialogOrRun(
                                                symCash >= BigDecimal(binding.debtAmount.text.toString()),
                                                it,
                                                "update"
                                            )
                                        }
                                    } else {
                                        if (binding.radioCashType.isChecked) {
                                            showDialogOrRun(
                                                symCash.add(existAmount) >= BigDecimal(binding.debtAmount.text.toString()),
                                                it,
                                                "update"
                                            )
                                        } else {
                                            showDialogOrRun(
                                                symCard >= BigDecimal(binding.debtAmount.text.toString()),
                                                it,
                                                "update"
                                            )
                                        }
                                    }
                                } else {
                                    viewModel.updateDebt()
                                }
                            }
                    )
                }
            }
        }

        //устанавливаем листенер на кнопку удаления для вызова подтверждения перед удалением
        binding.buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setCancelable(false)
            builder.setMessage(App.instance.getString(R.string.dialogDeleteDebt))
            builder.setPositiveButton(App.instance.getString(R.string.dialogButtonYes)) { dialog, _ ->
                if (existType == "toMe") {
                    viewModel.deleteDebt()
                } else {
                    if (existAccount == "card") {
                        showDialogOrRun(symCard >= existAmount, it, "delete")
                    } else {
                        showDialogOrRun(symCash >= existAmount, it, "delete")
                    }
                }
                dialog!!.cancel()
            }
            builder.setNegativeButton(App.instance.getString(R.string.dialogButtonNo)) { dialog, _ ->
                dialog!!.cancel()
            }
            builder.create().show()
        }

        //устанавливаем листенер на ввод суммы для ее корректного ввода
        binding.debtAmount.addTextChangedListener(object : TextWatcher {
            val pattern = Pattern.compile("\\d{0,7}?(\\.\\d{0,2})?")

            override fun afterTextChanged(s: Editable?) {
                val matcher = pattern.matcher(s)
                if (!matcher.matches()) {
                    if (countDialogShow < 1) {
                        val builder = AlertDialog.Builder(binding.debtAmount.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.errorAmountEnteredTitle))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                    countDialogShow++
                    s!!.delete(s.lastIndex, s.lastIndex + 1)
                }
                countDialogShow = 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    //если условие выполняется - выполняем действие
    //если не выполняется - грузим настройку и показываем предупреждение
    private fun showDialogOrRun(equation: Boolean, view: View, category: String) {
        if (equation) {
            when (category) {
                "add" -> viewModel.addDebt()
                "update" -> viewModel.updateDebt()
                else -> viewModel.deleteDebt()
            }
        } else {
            addToDisposable(
                loadSettingUseCase.load(Setting("5", 0))
                    .subscribeBy { fifthSetting ->
                        if (fifthSetting.value == 1) {
                            val dialog = Dialog(view.context)
                            dialog.setCancelable(false)
                            dialog.setContentView(R.layout.dialog_show_warning)
                            val messageTextView = dialog.findViewById<TextView>(R.id.warningMessage)
                            messageTextView.setText(R.string.warningLowExistBalance)
                            val checkBox = dialog.findViewById<CheckBox>(R.id.warningCheck)
                            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                            cancelButton.setOnClickListener {
                                dialog.dismiss()
                            }
                            val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
                            positiveButton.setOnClickListener {
                                if (checkBox.isChecked) {
                                    addToDisposable(
                                        saveSettingUseCase.save(Setting("5", 0))
                                            .subscribeBy()
                                    )
                                }
                                when (category) {
                                    "add" -> viewModel.addDebt()
                                    "update" -> viewModel.updateDebt()
                                    else -> viewModel.deleteDebt()
                                }
                                dialog.dismiss()
                            }
                            dialog.show()
                        } else {
                            when (category) {
                                "add" -> viewModel.addDebt()
                                "update" -> viewModel.updateDebt()
                                else -> viewModel.deleteDebt()
                            }
                        }
                    }
            )
        }
    }
}