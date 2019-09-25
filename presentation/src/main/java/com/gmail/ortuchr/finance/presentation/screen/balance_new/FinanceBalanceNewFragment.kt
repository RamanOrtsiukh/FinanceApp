package com.gmail.ortuchr.finance.presentation.screen.balance_new

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import com.gmail.ortuchr.finance.databinding.FragmentFinanceBalanceNewBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class FinanceBalanceNewFragment : BaseMvvmFragment<
        FinanceBalanceNewViewModel,
        FinanceRouter,
        FragmentFinanceBalanceNewBinding>() {

    companion object {
        private const val ID_EXTRA = "ID_EXTRA"
        private const val SYM_CARD_EXTRA = "SYM_CARD_EXTRA"
        private const val SYM_CASH_EXTRA = "SYM_CASH_EXTRA"
        private const val EXIST_AMOUNT_EXTRA = "EXIST_AMOUNT_EXTRA"
        private const val EXIST_ACCOUNT_EXTRA = "EXIST_ACCOUNT_EXTRA"

        fun getInstance(id: String, symCard: String, symCash: String, existAmount: String, existAccount: String): FinanceBalanceNewFragment {
            val fragment = FinanceBalanceNewFragment()
            val bundle = Bundle()
            bundle.putString(ID_EXTRA, id)
            bundle.putString(SYM_CARD_EXTRA, symCard)
            bundle.putString(SYM_CASH_EXTRA, symCash)
            bundle.putString(EXIST_AMOUNT_EXTRA, existAmount)
            bundle.putString(EXIST_ACCOUNT_EXTRA, existAccount)
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

    override fun provideViewModel(): FinanceBalanceNewViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceBalanceNewViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_balance_new

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загружаем аргументы
        val id = arguments?.getString(ID_EXTRA, "")
        val symCard = BigDecimal(arguments?.getString(SYM_CARD_EXTRA, "0.00"))
        val symCash = BigDecimal(arguments?.getString(SYM_CASH_EXTRA, "0.00"))
        val existAmount = BigDecimal(arguments?.getString(EXIST_AMOUNT_EXTRA, "0.00"))
        val existAccount = arguments?.getString(EXIST_ACCOUNT_EXTRA, "card")
        viewModel.isNewIncome(id!!)

        //загружаем настройку, если да - показываем подсказку
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintBalanceNewTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintBalanceNewMessage)
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

        //устанавливаем листенер на дату для ее изменения
        binding.textViewDate.setOnClickListener {

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
                    viewModel.dateChange(year, month, day)
                }
            }

            //запускаем диалог
            val newFragment = DatePickerFragment()
            newFragment.isCancelable = false
            newFragment.show(activity!!.supportFragmentManager, "datePicker")
        }

        //устанавливаем листенер на добавление баланса, для отображения невозможности перевода
        binding.buttonAdd.setOnClickListener {
            if (binding.editTextAmount.text.isNullOrEmpty() || BigDecimal(binding.editTextAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )
            ) {
                val builder = AlertDialog.Builder(it.context)
                builder.setCancelable(false)
                builder.setMessage(App.instance.getString(R.string.amountNotFoundTitle))
                builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                    dialog!!.cancel()
                }
                builder.create().show()
            } else {
                if (binding.radioAddToCard.isChecked || binding.radioAddToCash.isChecked) {
                    viewModel.addIncome()
                }
                if (binding.radioChangeToCash.isChecked) {
                    if (BigDecimal(binding.editTextAmount.text.toString()) > symCard) {
                        addToDisposable(
                            loadSettingUseCase.load(Setting("5", 0))
                                .subscribeBy { fifthSetting ->
                                    if (fifthSetting.value == 1) {
                                        val dialog = Dialog(it.context)
                                        dialog.setCancelable(false)
                                        dialog.setContentView(R.layout.dialog_show_warning)
                                        val messageTextView = dialog.findViewById<TextView>(R.id.warningMessage)
                                        messageTextView.setText(R.string.warningErrorLowCard)
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
                                            viewModel.addTransfer("card", "cash")
                                            dialog.dismiss()
                                        }
                                        dialog.show()
                                    } else {
                                        viewModel.addTransfer("card", "cash")
                                    }
                                }
                        )
                    } else {
                        viewModel.addTransfer("card", "cash")
                    }
                }
                if (binding.radioChangeToCard.isChecked) {
                    if (BigDecimal(binding.editTextAmount.text.toString()) > symCash) {
                        addToDisposable(
                            loadSettingUseCase.load(Setting("5", 0))
                                .subscribeBy { fifthSetting ->
                                    if (fifthSetting.value == 1) {
                                        val dialog = Dialog(it.context)
                                        dialog.setCancelable(false)
                                        dialog.setContentView(R.layout.dialog_show_warning)
                                        val messageTextView = dialog.findViewById<TextView>(R.id.warningMessage)
                                        messageTextView.setText(R.string.warningErrorLowCash)
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
                                            viewModel.addTransfer("cash", "card")
                                            dialog.dismiss()
                                        }
                                        dialog.show()
                                    } else {
                                        viewModel.addTransfer("cash", "card")
                                    }
                                }
                        )
                    } else {
                        viewModel.addTransfer("cash", "card")
                    }
                }
            }
        }

        //устанавливаем листенер на изменение баланса, для отображения невозможности перевода
        binding.buttonChange.setOnClickListener {
            if (binding.editTextAmount.text.isNullOrEmpty() || BigDecimal(binding.editTextAmount.text.toString()) <= BigDecimal(
                    "0.00"
                )
            ) {
                val builder = AlertDialog.Builder(it.context)
                builder.setCancelable(false)
                builder.setMessage(App.instance.getString(R.string.amountNotFoundTitle))
                builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                    dialog!!.cancel()
                }
                builder.create().show()
            } else {
                if (existAccount == "card") {
                    if (binding.radioAddToCard.isChecked) {
                        showDialogOrRun(symCard.add(BigDecimal(binding.editTextAmount.text.toString())) >= existAmount, it, "update")
                    } else {
                        showDialogOrRun(symCard >= existAmount, it, "update")
                    }
                } else {
                    if (binding.radioAddToCash.isChecked) {
                        showDialogOrRun(symCash.add(BigDecimal(binding.editTextAmount.text.toString())) >= existAmount, it, "update")
                    } else {
                        showDialogOrRun(symCash >= existAmount, it, "update")
                    }
                }
            }
        }

        //устанавливаем листенер на кнопку удаления для вызова подтверждения перед удалением
        binding.buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setCancelable(false)
            builder.setMessage(App.instance.getString(R.string.dialogDeleteBalance))
            builder.setPositiveButton(App.instance.getString(R.string.dialogButtonYes)) { dialog, _ ->
                if (existAccount == "card") {
                    showDialogOrRun(symCard >= existAmount, it, "delete")
                } else {
                    showDialogOrRun(symCash >= existAmount, it, "delete")
                }
                dialog!!.cancel()
            }
            builder.setNegativeButton(App.instance.getString(R.string.dialogButtonNo)) { dialog, _ ->
                dialog!!.cancel()
            }
            builder.create().show()
        }

        //устанавливаем листенер на ввод суммы для ее корректного ввода
        binding.editTextAmount.addTextChangedListener(object : TextWatcher {
            val pattern = Pattern.compile("\\d{0,7}?(\\.\\d{0,2})?")

            override fun afterTextChanged(s: Editable?) {
                val matcher = pattern.matcher(s)
                if (!matcher.matches()) {
                    if (countDialogShow < 1) {
                        val builder = AlertDialog.Builder(binding.editTextAmount.context)
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

        //снимаем фокус с ввода коментария, если поле убирается
        binding.radioChangeToCash.setOnClickListener {
            if (binding.addBalanceComment.hasFocus())
                hideKeyboard(view)
        }

        //снимаем фокус с ввода коментария, если поле убирается
        binding.radioChangeToCard.setOnClickListener {
            if (binding.addBalanceComment.hasFocus())
                hideKeyboard(view)
        }
    }

    //функция скрывания клавиатуры
    private fun hideKeyboard(view: View) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //если условие проходит - выполняем действие
    //если не проходит - грузим настройку и выводим предупреждение
    private fun showDialogOrRun(equation: Boolean, view: View, category: String) {
        if (equation) {
            when (category) {
                "update" -> viewModel.updateIncome()
                else -> viewModel.deleteIncome()
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
                                    "update" -> viewModel.updateIncome()
                                    else -> viewModel.deleteIncome()
                                }
                                dialog.dismiss()
                            }
                            dialog.show()
                        } else {
                            when (category) {
                                "update" -> viewModel.updateIncome()
                                else -> viewModel.deleteIncome()
                            }
                        }
                    }
            )
        }
    }
}

