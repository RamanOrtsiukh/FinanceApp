package com.gmail.ortuchr.finance.presentation.screen.debts_more

import android.app.Dialog
import android.os.Bundle
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
import com.gmail.ortuchr.finance.databinding.FragmentFinanceDebtsMoreBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class FinanceDebtsMoreFragment: BaseMvvmFragment<
        FinanceDebtsMoreViewModel,
        FinanceRouter,
        FragmentFinanceDebtsMoreBinding>() {

    companion object {
        fun getInstance(): FinanceDebtsMoreFragment = FinanceDebtsMoreFragment()
    }

    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase

    init {
        App.appComponent.inject(this)
    }

    override fun provideViewModel(): FinanceDebtsMoreViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceDebtsMoreViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_debts_more

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
                        titleTextView.setText(R.string.hintDebtsMoreTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintDebtsMoreMessage)
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

        //устанавливаем адаптер на долги мне
        binding.recyclerDebtsToMe.adapter = viewModel.adapterDebtToMe
        binding.recyclerDebtsToMe.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.recyclerDebtsToMe.setHasFixedSize(true)

        //устанавливаем адаптер на мои долги
        binding.recyclerDebtsFromMe.adapter = viewModel.adapterDebtFromMe
        binding.recyclerDebtsFromMe.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        binding.recyclerDebtsFromMe.setHasFixedSize(true)

        //загружаем данные во вьюмодели
        viewModel.setData()
    }
}