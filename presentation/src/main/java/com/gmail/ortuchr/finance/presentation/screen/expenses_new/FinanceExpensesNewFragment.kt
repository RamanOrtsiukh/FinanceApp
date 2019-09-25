package com.gmail.ortuchr.finance.presentation.screen.expenses_new

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.category.ChangeCategoryInRecordsUseCase
import com.gmail.ortuchr.domain.usecases.category.LoadCategoryUseCase
import com.gmail.ortuchr.domain.usecases.category.UpdateCategoryTitleUseCase
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.databinding.FragmentFinanceExpensesNewBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import java.math.BigDecimal
import java.util.*
import java.util.regex.Pattern
import javax.inject.Inject

class FinanceExpensesNewFragment : BaseMvvmFragment<
        FinanceExpensesNewViewModel,
        FinanceRouter,
        FragmentFinanceExpensesNewBinding>() {

    companion object {
        private const val ID_EXTRA = "ID_EXTRA"
        private const val SYM_CARD_EXTRA = "SYM_CARD_EXTRA"
        private const val SYM_CASH_EXTRA = "SYM_CASH_EXTRA"
        private const val EXIST_AMOUNT_EXTRA = "EXIST_AMOUNT_EXTRA"
        private const val EXIST_ACCOUNT_EXTRA = "EXIST_ACCOUNT_EXTRA"

        fun getInstance(id: String, symCard: String, symCash: String, existAmount: String, existAccount: String): FinanceExpensesNewFragment {
            val fragment = FinanceExpensesNewFragment()
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
    @Inject
    lateinit var loadCategoryUseCase: LoadCategoryUseCase
    @Inject
    lateinit var updateCategoryTitleUseCase: UpdateCategoryTitleUseCase
    @Inject
    lateinit var changeCategoryInRecordsUseCase: ChangeCategoryInRecordsUseCase

    init {
        App.appComponent.inject(this)
    }

    override fun provideViewModel(): FinanceExpensesNewViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceExpensesNewViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_expenses_new

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загружаем категории
        val id = arguments?.getString(ID_EXTRA, "")
        var symCard = BigDecimal(arguments?.getString(SYM_CARD_EXTRA, "0.00"))
        var symCash = BigDecimal(arguments?.getString(SYM_CASH_EXTRA, "0.00"))
        val existAmount = BigDecimal(arguments?.getString(EXIST_AMOUNT_EXTRA, "0.00"))
        val existAccount = arguments?.getString(EXIST_ACCOUNT_EXTRA, "card")
        viewModel.isNewExpense(id!!)

        //загружаем настройку, если да - показываем подсказку
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintExpensesNewTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintExpensesNewMessage)
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

        //устанавливаем листенер на добавление, для показа предупреждения минусового баланса
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
                val presentValue = when (binding.radioCardType.isChecked) {
                    true -> symCard
                    else -> symCash
                }
                if (BigDecimal(binding.editTextAmount.text.toString()) <= presentValue) {
                    when (binding.radioCardType.isChecked) {
                        true -> symCard = symCard.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                        else -> symCash = symCash.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                    }
                    viewModel.addExpense()
                    hideKeyboard(view)
                } else {
                    addToDisposable(
                        loadSettingUseCase.load(Setting("5", 0))
                            .subscribeBy { fifthSetting ->
                                if (fifthSetting.value == 1) {
                                    val dialog = Dialog(it.context)
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
                                        when (binding.radioCardType.isChecked) {
                                            true -> symCard =
                                                symCard.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                                            else -> symCash =
                                                symCash.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                                        }
                                        viewModel.addExpense()
                                        hideKeyboard(view)
                                        dialog.dismiss()
                                    }
                                    dialog.show()
                                } else {
                                    when (binding.radioCardType.isChecked) {
                                        true -> symCard =
                                            symCard.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                                        else -> symCash =
                                            symCash.subtract(BigDecimal(binding.editTextAmount.text.toString()))
                                    }
                                    viewModel.addExpense()
                                    hideKeyboard(view)
                                }
                            }
                    )
                }
            }
        }

        //устанавливаем листенер на изменение, для показа предупреждения минусового баланса
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
                    if (binding.radioCardType.isChecked) {
                        showDialogOrRun(symCard.add(existAmount) >= BigDecimal(binding.editTextAmount.text.toString()), it)
                    } else {
                        showDialogOrRun(symCash >= BigDecimal(binding.editTextAmount.text.toString()), it)
                    }
                } else {
                    if (binding.radioCashType.isChecked) {
                        showDialogOrRun(symCash.add(existAmount) >= BigDecimal(binding.editTextAmount.text.toString()), it)
                    } else {
                        showDialogOrRun(symCard >= BigDecimal(binding.editTextAmount.text.toString()), it)
                    }
                }
            }
        }

        //устанавливаем листенер на кнопку удаления для вызова подтверждения перед удалением
        binding.buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setCancelable(false)
            builder.setMessage(App.instance.getString(R.string.dialogDeleteExpense))
            builder.setPositiveButton(App.instance.getString(R.string.dialogButtonYes)) { dialog, _ ->
                viewModel.deleteExpense()
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

        //устанавливаем листенер на изменение названия категории
        binding.buttonChangeCategory.setOnClickListener {

            val selectedRadioButton: RadioButton
            val selectedIndex: Int
            var titleText = ""
            if (binding.radioGroupExpenseChoice1.checkedRadioButtonId != 0) {
                selectedRadioButton =
                    binding.radioGroupExpenseChoice1.findViewById(binding.radioGroupExpenseChoice1.checkedRadioButtonId)
                selectedIndex = binding.radioGroupExpenseChoice1.indexOfChild(selectedRadioButton) + 1
            } else {
                selectedRadioButton =
                    binding.radioGroupExpenseChoice2.findViewById(binding.radioGroupExpenseChoice2.checkedRadioButtonId)
                selectedIndex = binding.radioGroupExpenseChoice2.indexOfChild(selectedRadioButton) + 8
            }
            addToDisposable(
                loadCategoryUseCase.load(Category(selectedIndex.toString(), "", 1))
                    .subscribeBy(onNext = {
                        titleText = it.title
                    }, onComplete = {
                        val dialog = Dialog(it.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_category_change)
                        val titleEditText = dialog.findViewById<EditText>(R.id.titleEditText)
                        titleEditText.setText(titleText)
                        //устанавливаем название по умолчанию
                        val neutralButton = dialog.findViewById<Button>(R.id.neutralButton)
                        neutralButton.setOnClickListener {
                            val returnTitle = when (selectedIndex) {
                                1 -> App.instance.getString(R.string.choice1Title)
                                2 -> App.instance.getString(R.string.choice2Title)
                                3 -> App.instance.getString(R.string.choice3Title)
                                4 -> App.instance.getString(R.string.choice4Title)
                                5 -> App.instance.getString(R.string.choice5Title)
                                6 -> App.instance.getString(R.string.choice6Title)
                                7 -> App.instance.getString(R.string.choice7Title)
                                8 -> App.instance.getString(R.string.choice8Title)
                                9 -> App.instance.getString(R.string.choice9Title)
                                10 -> App.instance.getString(R.string.choice10Title)
                                11 -> App.instance.getString(R.string.choice11Title)
                                12 -> App.instance.getString(R.string.choice12Title)
                                13 -> App.instance.getString(R.string.choice13Title)
                                14 -> App.instance.getString(R.string.choice14Title)
                                else -> App.instance.getString(R.string.choice1Title)
                            }
                            titleEditText.setText(returnTitle)
                            titleEditText.setSelection(returnTitle.length)
                        }
                        //закрываем диалог
                        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
                        cancelButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        //меняем название категории
                        val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
                        positiveButton.setOnClickListener {
                            if (titleEditText.text.isNullOrEmpty()) {
                                val builder = AlertDialog.Builder(it.context)
                                builder.setCancelable(false)
                                builder.setMessage(App.instance.getString(R.string.changeCategoryTitleError))
                                builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialogError, _ ->
                                    dialogError!!.cancel()
                                }
                                builder.create().show()
                            } else {
                                val builder = AlertDialog.Builder(it.context)
                                builder.setMessage(App.instance.getString(R.string.changeCategoryTitleMessage))
                                builder.setPositiveButton(App.instance.getString(R.string.dialogButtonYes)) { dialogPositive, _ ->
                                    addToDisposable(
                                        updateCategoryTitleUseCase.update(
                                            Category(
                                                selectedIndex.toString(),
                                                titleEditText.text.toString(),
                                                1
                                            )
                                        )
                                            .subscribeBy {
                                                addToDisposable(
                                                    changeCategoryInRecordsUseCase.change(
                                                        Category("0", titleText, 1),
                                                        Category("0", titleEditText.text.toString(), 1)
                                                    )
                                                        .subscribeBy {
                                                            viewModel.loadCategories()
                                                            Toast.makeText(
                                                                view.context,
                                                                App.instance.getString(R.string.changeCategoryTitleSuccess),
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            dialogPositive!!.cancel()
                                                            dialog.dismiss()
                                                        }
                                                )
                                            }
                                    )
                                }
                                builder.setNegativeButton(App.instance.getString(R.string.dialogButtonNo)) { dialogNegative, _ ->
                                    dialogNegative!!.cancel()
                                }
                                builder.create().show()
                            }
                        }
                        dialog.show()
                    })
            )
        }
    }

    //функция скрывания клавиатуры
    private fun hideKeyboard(view: View) {
        val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    //если условие выполняется - выполняем действие
    //если не выполняется - грузим настройку и показываем предупреждение
    private fun showDialogOrRun(equation: Boolean, view: View) {
        if (equation) {
            viewModel.updateExpense()
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
                                viewModel.updateExpense()
                                dialog.dismiss()
                            }
                            dialog.show()
                        } else {
                            viewModel.updateExpense()
                        }
                    }
            )
        }
    }
}