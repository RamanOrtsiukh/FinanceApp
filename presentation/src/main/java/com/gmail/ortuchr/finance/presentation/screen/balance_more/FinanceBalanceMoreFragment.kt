package com.gmail.ortuchr.finance.presentation.screen.balance_more

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.usecases.setting.LoadSettingUseCase
import com.gmail.ortuchr.domain.usecases.setting.SaveSettingUseCase
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.app.App
import com.gmail.ortuchr.finance.databinding.FragmentFinanceBalanceMoreBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class FinanceBalanceMoreFragment : BaseMvvmFragment<
        FinanceBalanceMoreViewModel,
        FinanceRouter,
        FragmentFinanceBalanceMoreBinding>() {

    companion object {
        fun getInstance(): FinanceBalanceMoreFragment = FinanceBalanceMoreFragment()
    }

    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase

    init {
        App.appComponent.inject(this)
    }

    override fun provideViewModel(): FinanceBalanceMoreViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceBalanceMoreViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_balance_more

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //загружаем настройку и если да - показываем подсказку
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintBalanceMoreTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintBalanceMoreMessage)
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

        //загружаем данные во вьюмодели
        viewModel.showData()

        //устанавливаем листенер на ввод настройки с датой месяца
        binding.editTextStartDay.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    if (s.toString().toInt() in 1..31) viewModel.saveSettingDayFrom(s.toString().toInt())
                    else {
                        val builder = AlertDialog.Builder(view.context)
                        builder.setCancelable(false)
                        builder.setMessage(App.instance.getString(R.string.dialogDateError))
                        builder.setPositiveButton(App.instance.getString(R.string.dialogButtonOk)) { dialog, _ ->
                            s.delete(s.lastIndex, s.lastIndex + 1)
                            dialog!!.cancel()
                        }
                        builder.create().show()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })

        //диалог на подтверждение удаления всех данных
        binding.buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setCancelable(false)
            builder.setMessage(App.instance.getString(R.string.dialogDeleteAll))
            builder.setPositiveButton(App.instance.getString(R.string.dialogButtonYes)) { dialog, _ ->
                viewModel.deleteData()
                dialog!!.cancel()
            }
            builder.setNegativeButton(App.instance.getString(R.string.dialogButtonNo)) { dialog, _ ->
                dialog!!.cancel()
            }
            builder.create().show()
        }
    }
}