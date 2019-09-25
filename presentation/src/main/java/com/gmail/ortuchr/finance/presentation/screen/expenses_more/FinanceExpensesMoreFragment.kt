package com.gmail.ortuchr.finance.presentation.screen.expenses_more

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.DatePicker
import android.widget.TextView
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModelProviders
import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.category.LoadCategoryUseCase
import com.gmail.ortuchr.domain.usecases.category.UpdateCategoryCheckedUseCase
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.databinding.FragmentFinanceExpensesMoreBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class FinanceExpensesMoreFragment : BaseMvvmFragment<
        FinanceExpensesMoreViewModel,
        FinanceRouter,
        FragmentFinanceExpensesMoreBinding>() {

    companion object {
        fun getInstance(): FinanceExpensesMoreFragment = FinanceExpensesMoreFragment()
    }

    //обсерваблы для хранения названий категорий
    private val choiceTitleIncome = ObservableField<String>("income")
    private val choiceTitle1 = ObservableField<String>("")
    private val choiceTitle2 = ObservableField<String>("")
    private val choiceTitle3 = ObservableField<String>("")
    private val choiceTitle4 = ObservableField<String>("")
    private val choiceTitle5 = ObservableField<String>("")
    private val choiceTitle6 = ObservableField<String>("")
    private val choiceTitle7 = ObservableField<String>("")
    private val choiceTitle8 = ObservableField<String>("")
    private val choiceTitle9 = ObservableField<String>("")
    private val choiceTitle10 = ObservableField<String>("")
    private val choiceTitle11 = ObservableField<String>("")
    private val choiceTitle12 = ObservableField<String>("")
    private val choiceTitle13 = ObservableField<String>("")
    private val choiceTitle14 = ObservableField<String>("")
    //обсерваблы для хранения включено ли отслеживание этой категории
    private val checkTitleIncome = ObservableInt(1)
    private val checkTitle1 = ObservableInt(1)
    private val checkTitle2 = ObservableInt(1)
    private val checkTitle3 = ObservableInt(1)
    private val checkTitle4 = ObservableInt(1)
    private val checkTitle5 = ObservableInt(1)
    private val checkTitle6 = ObservableInt(1)
    private val checkTitle7 = ObservableInt(1)
    private val checkTitle8 = ObservableInt(1)
    private val checkTitle9 = ObservableInt(1)
    private val checkTitle10 = ObservableInt(1)
    private val checkTitle11 = ObservableInt(1)
    private val checkTitle12 = ObservableInt(1)
    private val checkTitle13 = ObservableInt(1)
    private val checkTitle14 = ObservableInt(1)


    @Inject
    lateinit var updateCategoryCheckedUseCase: UpdateCategoryCheckedUseCase
    @Inject
    lateinit var loadCategoryUseCase: LoadCategoryUseCase
    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase

    init {
        App.appComponent.inject(this)
    }

    override fun provideViewModel(): FinanceExpensesMoreViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceExpensesMoreViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_expenses_more

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загружаем настройку, если да - показываем подсказку
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintExpensesMoreTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintExpensesMoreMessage)
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

        //загружаем названия категорий
        viewModel.loadCategoriesTitle()

        //загружаем выбранные категории
        val choiceCategoryArrayList = ArrayList<String>(15)
        addToDisposable(
            loadCategoryUseCase.load(Category("15", "", 1))
                .subscribeBy(onNext = {
                    checkTitleIncome.set(it.checked)
                    if (it.checked == 1)
                        choiceCategoryArrayList.add(choiceTitleIncome.get()!!)
                }, onComplete = {
                    addToDisposable(
                        loadCategoryUseCase.load(Category("1", "", 1))
                            .subscribeBy(onNext = {
                                choiceTitle1.set(it.title)
                                checkTitle1.set(it.checked)
                                if (it.checked == 1)
                                    choiceCategoryArrayList.add(it.title)
                            }, onComplete = {
                                addToDisposable(
                                    loadCategoryUseCase.load(Category("2", "", 1))
                                        .subscribeBy(onNext = {
                                            choiceTitle2.set(it.title)
                                            checkTitle2.set(it.checked)
                                            if (it.checked == 1)
                                                choiceCategoryArrayList.add(it.title)
                                        }, onComplete = {
                                            addToDisposable(
                                                loadCategoryUseCase.load(Category("3", "", 1))
                                                    .subscribeBy(onNext = {
                                                        choiceTitle3.set(it.title)
                                                        checkTitle3.set(it.checked)
                                                        if (it.checked == 1)
                                                            choiceCategoryArrayList.add(it.title)
                                                    }, onComplete = {
                                                        addToDisposable(
                                                            loadCategoryUseCase.load(Category("4", "", 1))
                                                                .subscribeBy(onNext = {
                                                                    choiceTitle4.set(it.title)
                                                                    checkTitle4.set(it.checked)
                                                                    if (it.checked == 1)
                                                                        choiceCategoryArrayList.add(it.title)
                                                                }, onComplete = {
                                                                    addToDisposable(
                                                                        loadCategoryUseCase.load(Category("5", "", 1))
                                                                            .subscribeBy(onNext = {
                                                                                choiceTitle5.set(it.title)
                                                                                checkTitle5.set(it.checked)
                                                                                if (it.checked == 1)
                                                                                    choiceCategoryArrayList.add(it.title)
                                                                            }, onComplete = {
                                                                                addToDisposable(
                                                                                    loadCategoryUseCase.load(
                                                                                        Category(
                                                                                            "6",
                                                                                            "",
                                                                                            1
                                                                                        )
                                                                                    )
                                                                                        .subscribeBy(onNext = {
                                                                                            choiceTitle6.set(it.title)
                                                                                            checkTitle6.set(it.checked)
                                                                                            if (it.checked == 1)
                                                                                                choiceCategoryArrayList.add(
                                                                                                    it.title
                                                                                                )
                                                                                        }, onComplete = {
                                                                                            addToDisposable(
                                                                                                loadCategoryUseCase.load(
                                                                                                    Category("7", "", 1)
                                                                                                )
                                                                                                    .subscribeBy(onNext = {
                                                                                                        choiceTitle7.set(
                                                                                                            it.title
                                                                                                        )
                                                                                                        checkTitle7.set(
                                                                                                            it.checked
                                                                                                        )
                                                                                                        if (it.checked == 1)
                                                                                                            choiceCategoryArrayList.add(
                                                                                                                it.title
                                                                                                            )
                                                                                                    }, onComplete = {
                                                                                                        addToDisposable(
                                                                                                            loadCategoryUseCase.load(
                                                                                                                Category(
                                                                                                                    "8",
                                                                                                                    "",
                                                                                                                    1
                                                                                                                )
                                                                                                            )
                                                                                                                .subscribeBy(
                                                                                                                    onNext = {
                                                                                                                        choiceTitle8.set(
                                                                                                                            it.title
                                                                                                                        )
                                                                                                                        checkTitle8.set(
                                                                                                                            it.checked
                                                                                                                        )
                                                                                                                        if (it.checked == 1)
                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                it.title
                                                                                                                            )
                                                                                                                    },
                                                                                                                    onComplete = {
                                                                                                                        addToDisposable(
                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                Category(
                                                                                                                                    "9",
                                                                                                                                    "",
                                                                                                                                    1
                                                                                                                                )
                                                                                                                            )
                                                                                                                                .subscribeBy(
                                                                                                                                    onNext = {
                                                                                                                                        choiceTitle9.set(
                                                                                                                                            it.title
                                                                                                                                        )
                                                                                                                                        checkTitle9.set(
                                                                                                                                            it.checked
                                                                                                                                        )
                                                                                                                                        if (it.checked == 1)
                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                it.title
                                                                                                                                            )
                                                                                                                                    },
                                                                                                                                    onComplete = {
                                                                                                                                        addToDisposable(
                                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                                Category(
                                                                                                                                                    "10",
                                                                                                                                                    "",
                                                                                                                                                    1
                                                                                                                                                )
                                                                                                                                            )
                                                                                                                                                .subscribeBy(
                                                                                                                                                    onNext = {
                                                                                                                                                        choiceTitle10.set(
                                                                                                                                                            it.title
                                                                                                                                                        )
                                                                                                                                                        checkTitle10.set(
                                                                                                                                                            it.checked
                                                                                                                                                        )
                                                                                                                                                        if (it.checked == 1)
                                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                                it.title
                                                                                                                                                            )
                                                                                                                                                    },
                                                                                                                                                    onComplete = {
                                                                                                                                                        addToDisposable(
                                                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                                                Category(
                                                                                                                                                                    "11",
                                                                                                                                                                    "",
                                                                                                                                                                    1
                                                                                                                                                                )
                                                                                                                                                            )
                                                                                                                                                                .subscribeBy(
                                                                                                                                                                    onNext = {
                                                                                                                                                                        choiceTitle11.set(
                                                                                                                                                                            it.title
                                                                                                                                                                        )
                                                                                                                                                                        checkTitle11.set(
                                                                                                                                                                            it.checked
                                                                                                                                                                        )
                                                                                                                                                                        if (it.checked == 1)
                                                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                                                it.title
                                                                                                                                                                            )
                                                                                                                                                                    },
                                                                                                                                                                    onComplete = {
                                                                                                                                                                        addToDisposable(
                                                                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                                                                Category(
                                                                                                                                                                                    "12",
                                                                                                                                                                                    "",
                                                                                                                                                                                    1
                                                                                                                                                                                )
                                                                                                                                                                            )
                                                                                                                                                                                .subscribeBy(
                                                                                                                                                                                    onNext = {
                                                                                                                                                                                        choiceTitle12.set(
                                                                                                                                                                                            it.title
                                                                                                                                                                                        )
                                                                                                                                                                                        checkTitle12.set(
                                                                                                                                                                                            it.checked
                                                                                                                                                                                        )
                                                                                                                                                                                        if (it.checked == 1)
                                                                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                                                                it.title
                                                                                                                                                                                            )
                                                                                                                                                                                    },
                                                                                                                                                                                    onComplete = {
                                                                                                                                                                                        addToDisposable(
                                                                                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                                                                                Category(
                                                                                                                                                                                                    "13",
                                                                                                                                                                                                    "",
                                                                                                                                                                                                    1
                                                                                                                                                                                                )
                                                                                                                                                                                            )
                                                                                                                                                                                                .subscribeBy(
                                                                                                                                                                                                    onNext = {
                                                                                                                                                                                                        choiceTitle13.set(
                                                                                                                                                                                                            it.title
                                                                                                                                                                                                        )
                                                                                                                                                                                                        checkTitle13.set(
                                                                                                                                                                                                            it.checked
                                                                                                                                                                                                        )
                                                                                                                                                                                                        if (it.checked == 1)
                                                                                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                                                                                it.title
                                                                                                                                                                                                            )
                                                                                                                                                                                                    },
                                                                                                                                                                                                    onComplete = {
                                                                                                                                                                                                        addToDisposable(
                                                                                                                                                                                                            loadCategoryUseCase.load(
                                                                                                                                                                                                                Category(
                                                                                                                                                                                                                    "14",
                                                                                                                                                                                                                    "",
                                                                                                                                                                                                                    1
                                                                                                                                                                                                                )
                                                                                                                                                                                                            )
                                                                                                                                                                                                                .subscribeBy(
                                                                                                                                                                                                                    onNext = {
                                                                                                                                                                                                                        choiceTitle14.set(
                                                                                                                                                                                                                            it.title
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                        checkTitle14.set(
                                                                                                                                                                                                                            it.checked
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                        if (it.checked == 1)
                                                                                                                                                                                                                            choiceCategoryArrayList.add(
                                                                                                                                                                                                                                it.title
                                                                                                                                                                                                                            )
                                                                                                                                                                                                                    },
                                                                                                                                                                                                                    onComplete = {
                                                                                                                                                                                                                        val button = binding.buttonChoiceCategory
                                                                                                                                                                                                                        if (choiceCategoryArrayList.size != 15) {
                                                                                                                                                                                                                            button.text = App.instance.getString(R.string.choiceCategorySelectTitle)
                                                                                                                                                                                                                            button.setBackgroundResource(R.drawable.small_button_selector_choice)
                                                                                                                                                                                                                        } else {
                                                                                                                                                                                                                            button.text = App.instance.getString(R.string.choiceCategoryTitle)
                                                                                                                                                                                                                            button.setBackgroundResource(R.drawable.small_button_selector)
                                                                                                                                                                                                                        }
                                                                                                                                                                                                                        //показываем выбранные категории
                                                                                                                                                                                                                        viewModel.setCategories(
                                                                                                                                                                                                                            choiceCategoryArrayList
                                                                                                                                                                                                                        )
                                                                                                                                                                                                                    })
                                                                                                                                                                                                        )
                                                                                                                                                                                                    })
                                                                                                                                                                                        )
                                                                                                                                                                                    })
                                                                                                                                                                        )
                                                                                                                                                                    })
                                                                                                                                                        )
                                                                                                                                                    })
                                                                                                                                        )
                                                                                                                                    })
                                                                                                                        )
                                                                                                                    })
                                                                                                        )
                                                                                                    })
                                                                                            )
                                                                                        })
                                                                                )
                                                                            })
                                                                    )
                                                                })
                                                        )
                                                    })
                                            )
                                        })
                                )
                            })
                    )
                })
        )
        //устанавливаем адаптер для отображения
        binding.recyclerViewExpenses.adapter = viewModel.adapterBalance
        binding.recyclerViewExpenses.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.recyclerViewExpenses.setHasFixedSize(true)

        //устанавливаем листенер на дату начала для ее изменения
        binding.textViewSinceDate.setOnClickListener {

            class DatePickerFragment : androidx.fragment.app.DialogFragment(), DatePickerDialog.OnDateSetListener {

                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = 1

                    val dialog = DatePickerDialog(it.context, this, year, month, day)
                    dialog.datePicker.maxDate = c.timeInMillis
                    c.set(2000, Calendar.JANUARY, 1)
                    dialog.datePicker.minDate = c.timeInMillis
                    return dialog
                }

                //передаем полученную дату в вьюмодел
                override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.dateChange("from", year, month, day)

                    val c = Calendar.getInstance()
                    c.set(year, month, day)
                    val dateStart = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH).format(c.time)
                    val dateReturn = SimpleDateFormat("yy.MM.dd", Locale.ENGLISH).format(
                        SimpleDateFormat(
                            "dd.MM.yy",
                            Locale.ENGLISH
                        ).parse(binding.textViewToDate.text.toString())
                    )
                    //если дата начала поиска превышает дату окончания поиска выводим предупреждение
                    if (dateStart > dateReturn) {
                        val builder = AlertDialog.Builder(it.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.warningErrorDateExpense))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            dialog!!.cancel()
                        }
                        builder.create().show()
                        viewModel.dateChange("to", year, month, day)
                    }
                }
            }
            //запускаем диалог
            val newFragment = DatePickerFragment()
            newFragment.isCancelable = false
            newFragment.show(activity!!.supportFragmentManager, "dateFromPicker")
        }

        //устанавливаем листенер на дату окончания для ее изменения
        binding.textViewToDate.setOnClickListener {

            class DatePickerFragment : androidx.fragment.app.DialogFragment(), DatePickerDialog.OnDateSetListener {

                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                    val c = Calendar.getInstance()
                    val year = c.get(Calendar.YEAR)
                    val month = c.get(Calendar.MONTH)
                    val day = c.get(Calendar.DAY_OF_MONTH)

                    val dialog = DatePickerDialog(it.context, this, year, month, day)
                    val dateStart =
                        SimpleDateFormat("dd.MM.yy", Locale.ENGLISH).parse(binding.textViewSinceDate.text.toString())
                    dialog.datePicker.minDate = dateStart.time
                    return dialog
                }

                //передаем полученную дату в вьюмодел
                override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
                    viewModel.dateChange("to", year, month, day)
                }
            }

            //запускаем диалог
            val newFragment = DatePickerFragment()
            newFragment.isCancelable = false
            newFragment.show(activity!!.supportFragmentManager, "dateToPicker")
        }

        //устанавливаем листенер на кнопку выбора категорий
        binding.buttonChoiceCategory.setOnClickListener {

            var checkCount = 0

            val dialog = Dialog(it.context)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_category_choice)

            val checkIncome = dialog.findViewById<CheckBox>(R.id.cbIncome)
            val check1 = dialog.findViewById<CheckBox>(R.id.cb1)
            val check2 = dialog.findViewById<CheckBox>(R.id.cb2)
            val check3 = dialog.findViewById<CheckBox>(R.id.cb3)
            val check4 = dialog.findViewById<CheckBox>(R.id.cb4)
            val check5 = dialog.findViewById<CheckBox>(R.id.cb5)
            val check6 = dialog.findViewById<CheckBox>(R.id.cb6)
            val check7 = dialog.findViewById<CheckBox>(R.id.cb7)
            val check8 = dialog.findViewById<CheckBox>(R.id.cb8)
            val check9 = dialog.findViewById<CheckBox>(R.id.cb9)
            val check10 = dialog.findViewById<CheckBox>(R.id.cb10)
            val check11 = dialog.findViewById<CheckBox>(R.id.cb11)
            val check12 = dialog.findViewById<CheckBox>(R.id.cb12)
            val check13 = dialog.findViewById<CheckBox>(R.id.cb13)
            val check14 = dialog.findViewById<CheckBox>(R.id.cb14)

            //устанавливаем названия категорий в диалоге
            checkIncome.text = App.instance.getString(com.gmail.ortuchr.finance.R.string.incomeTitle)
            check1.text = choiceTitle1.get()
            check2.text = choiceTitle2.get()
            check3.text = choiceTitle3.get()
            check4.text = choiceTitle4.get()
            check5.text = choiceTitle5.get()
            check6.text = choiceTitle6.get()
            check7.text = choiceTitle7.get()
            check8.text = choiceTitle8.get()
            check9.text = choiceTitle9.get()
            check10.text = choiceTitle10.get()
            check11.text = choiceTitle11.get()
            check12.text = choiceTitle12.get()
            check13.text = choiceTitle13.get()
            check14.text = choiceTitle14.get()

            //устанавливаем выбрана ли категория в диалоге
            if (checkTitleIncome.get() == 1) checkIncome.isChecked = true
            if (checkTitle1.get() == 1) check1.isChecked = true
            if (checkTitle2.get() == 1) check2.isChecked = true
            if (checkTitle3.get() == 1) check3.isChecked = true
            if (checkTitle4.get() == 1) check4.isChecked = true
            if (checkTitle5.get() == 1) check5.isChecked = true
            if (checkTitle6.get() == 1) check6.isChecked = true
            if (checkTitle7.get() == 1) check7.isChecked = true
            if (checkTitle8.get() == 1) check8.isChecked = true
            if (checkTitle9.get() == 1) check9.isChecked = true
            if (checkTitle10.get() == 1) check10.isChecked = true
            if (checkTitle11.get() == 1) check11.isChecked = true
            if (checkTitle12.get() == 1) check12.isChecked = true
            if (checkTitle13.get() == 1) check13.isChecked = true
            if (checkTitle14.get() == 1) check14.isChecked = true

            //выбираем все категории или сбрасываем все категории
            val neutralButton = dialog.findViewById<Button>(R.id.neutralButton)
            neutralButton.setOnClickListener {
                if (checkCount % 2 == 0) {
                    checkIncome.isChecked = true
                    check1.isChecked = true
                    check2.isChecked = true
                    check3.isChecked = true
                    check4.isChecked = true
                    check5.isChecked = true
                    check6.isChecked = true
                    check7.isChecked = true
                    check8.isChecked = true
                    check9.isChecked = true
                    check10.isChecked = true
                    check11.isChecked = true
                    check12.isChecked = true
                    check13.isChecked = true
                    check14.isChecked = true
                    checkCount++
                } else {
                    checkIncome.isChecked = false
                    check1.isChecked = false
                    check2.isChecked = false
                    check3.isChecked = false
                    check4.isChecked = false
                    check5.isChecked = false
                    check6.isChecked = false
                    check7.isChecked = false
                    check8.isChecked = false
                    check9.isChecked = false
                    check10.isChecked = false
                    check11.isChecked = false
                    check12.isChecked = false
                    check13.isChecked = false
                    check14.isChecked = false
                    checkCount++
                }
            }
            //закрываем диалог
            val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
            cancelButton.setOnClickListener {
                dialog.dismiss()
            }
            val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
            positiveButton.setOnClickListener {
                //обновляем чек категории, если он поменялся
                updateCategoryChecked(checkIncome, 0, checkTitleIncome, 15)
                updateCategoryChecked(check1, 1, checkTitle1)
                updateCategoryChecked(check2, 2, checkTitle2)
                updateCategoryChecked(check3, 3, checkTitle3)
                updateCategoryChecked(check4, 4, checkTitle4)
                updateCategoryChecked(check5, 5, checkTitle5)
                updateCategoryChecked(check6, 6, checkTitle6)
                updateCategoryChecked(check7, 7, checkTitle7)
                updateCategoryChecked(check8, 8, checkTitle8)
                updateCategoryChecked(check9, 9, checkTitle9)
                updateCategoryChecked(check10, 10, checkTitle10)
                updateCategoryChecked(check11, 11, checkTitle11)
                updateCategoryChecked(check12, 12, checkTitle12)
                updateCategoryChecked(check13, 13, checkTitle13)
                updateCategoryChecked(check14, 14, checkTitle14)

                //добавляем в список названия выбранных категорий
                choiceCategoryArrayList.clear()
                updateChoiceCategoryArrayList(checkIncome, choiceCategoryArrayList, choiceTitleIncome)
                updateChoiceCategoryArrayList(check1, choiceCategoryArrayList, choiceTitle1)
                updateChoiceCategoryArrayList(check2, choiceCategoryArrayList, choiceTitle2)
                updateChoiceCategoryArrayList(check3, choiceCategoryArrayList, choiceTitle3)
                updateChoiceCategoryArrayList(check4, choiceCategoryArrayList, choiceTitle4)
                updateChoiceCategoryArrayList(check5, choiceCategoryArrayList, choiceTitle5)
                updateChoiceCategoryArrayList(check6, choiceCategoryArrayList, choiceTitle6)
                updateChoiceCategoryArrayList(check7, choiceCategoryArrayList, choiceTitle7)
                updateChoiceCategoryArrayList(check8, choiceCategoryArrayList, choiceTitle8)
                updateChoiceCategoryArrayList(check9, choiceCategoryArrayList, choiceTitle9)
                updateChoiceCategoryArrayList(check10, choiceCategoryArrayList, choiceTitle10)
                updateChoiceCategoryArrayList(check11, choiceCategoryArrayList, choiceTitle11)
                updateChoiceCategoryArrayList(check12, choiceCategoryArrayList, choiceTitle12)
                updateChoiceCategoryArrayList(check13, choiceCategoryArrayList, choiceTitle13)
                updateChoiceCategoryArrayList(check14, choiceCategoryArrayList, choiceTitle14)

                //меняем вид кнопки, если выбран фильтр
                val button = binding.buttonChoiceCategory
                if (choiceCategoryArrayList.size != 15) {
                    button.text = App.instance.getString(R.string.choiceCategorySelectTitle)
                    button.setBackgroundResource(R.drawable.small_button_selector_choice)
                } else {
                    button.text = App.instance.getString(R.string.choiceCategoryTitle)
                    button.setBackgroundResource(R.drawable.small_button_selector)
                }
                viewModel.setCategories(choiceCategoryArrayList)
                dialog.dismiss()
            }
            dialog.show()
        }
    }

    //функция обновления чека категории, если он поменялся
    private fun updateCategoryChecked(
        check: CheckBox,
        idArray: Int,
        checkTitle: ObservableInt,
        idDB: Int = idArray
    ) {
        if (check.isChecked) {
            if (checkTitle.get() == 0) {
                addToDisposable(
                    updateCategoryCheckedUseCase.update(Category(idDB.toString(), "", 1))
                        .subscribeBy()
                )
                checkTitle.set(1)
            }
        } else {
            if (checkTitle.get() == 1) {
                addToDisposable(
                    updateCategoryCheckedUseCase.update(Category(idDB.toString(), "", 0))
                        .subscribeBy()
                )
                checkTitle.set(0)
            }
        }
    }

    //функция добавления в список названия выбранных категорий
    private fun updateChoiceCategoryArrayList(
        check: CheckBox,
        choiceArray: ArrayList<String>,
        choiceTitle: ObservableField<String>
    ) {
        if (check.isChecked)
            choiceArray.add(choiceTitle.get()!!)
    }
}