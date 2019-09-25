package com.gmail.ortuchr.finance.presentation.screen.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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
import com.gmail.ortuchr.finance.databinding.FragmentFinanceMainBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmFragment
import com.gmail.ortuchr.finance.presentation.screen.FinanceRouter
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import java.math.BigDecimal
import javax.inject.Inject


class FinanceMainFragment : BaseMvvmFragment<
        FinanceMainViewModel,
        FinanceRouter,
        FragmentFinanceMainBinding>() {

    @Inject
    lateinit var loadSettingUseCase: LoadSettingUseCase
    @Inject
    lateinit var saveSettingUseCase: SaveSettingUseCase

    //инициализация даггера
    init {
        App.appComponent.inject(this)
    }

    companion object {
        fun getInstance(): FinanceMainFragment = FinanceMainFragment()
    }

    override fun provideViewModel(): FinanceMainViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceMainViewModel::class.java)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_finance_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //из шаред преференсес берем первый ли это вход в программу
        val prefs = App.instance.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
        val prefsSettingName = "FIRST_RUN"
        val prefsSettingAd = "AD_TYPE"

        var firstRun = prefs.getBoolean(prefsSettingName, true)
        val adType = prefs.getString(prefsSettingAd, "npa")
        val publishSubject = PublishSubject.create<Boolean>()
        addToDisposable(
            publishSubject.subscribeBy {
                if (!it) viewModel.showData()
            }
        )

        //если первый вход в программу показываем приветственное окно и сохраняем базовые настройки
        if (firstRun) {
            val builderPrivacy = AlertDialog.Builder(view.context)
            builderPrivacy.setCancelable(false)
            builderPrivacy.setTitle(App.instance.getString(R.string.privacyTitle))
            builderPrivacy.setMessage(App.instance.getString(R.string.privacyMessage))
            builderPrivacy.setPositiveButton(App.instance.getString(R.string.privacyAccept)) { privacyDialog, _ ->
                privacyDialog.cancel()
                val builderChoice = AlertDialog.Builder(view.context)
                builderChoice.setCancelable(false)
                builderChoice.setTitle(App.instance.getString(R.string.privacyChoiceTitle))
                builderChoice.setMessage(App.instance.getString(R.string.privacyChoiceMessage))
                builderChoice.setPositiveButton(App.instance.getString(R.string.privacyChoicePersTitle)) { choiceDialog, _ ->
                    prefs.edit().putString(prefsSettingAd, "pa").apply()
                    choiceDialog.cancel()
                    val builder = AlertDialog.Builder(view.context)
                    builder.setCancelable(false)
                    builder.setTitle(App.instance.getString(R.string.firstRunTitle))
                    builder.setMessage(App.instance.getString(R.string.firstRunMessage))
                    builder.setPositiveButton(App.instance.getString(R.string.firstRunAnswer)) { dialog, _ ->
                        showAd(binding.bottomBanner, "pa")
                        showHint(view)
                        dialog!!.cancel()
                    }
                    builder.create().show()
                    addToDisposable(
                        Completable.fromAction {
                            viewModel.firstRun()
                        }.subscribeBy(onComplete = {
                            prefs.edit().putBoolean(prefsSettingName, false).apply()
                            firstRun = false
                            publishSubject.onNext(firstRun)
                        })
                    )
                }
                builderChoice.setNegativeButton(App.instance.getString(R.string.privacyChoiceNotPersTitle)) { choiceDialog, _ ->
                    prefs.edit().putString(prefsSettingAd, "npa").apply()
                    choiceDialog.cancel()
                    val builder = AlertDialog.Builder(view.context)
                    builder.setCancelable(false)
                    builder.setTitle(App.instance.getString(R.string.firstRunTitle))
                    builder.setMessage(App.instance.getString(R.string.firstRunMessage))
                    builder.setPositiveButton(App.instance.getString(R.string.firstRunAnswer)) { dialog, _ ->
                        showAd(binding.bottomBanner, "npa")
                        showHint(view)
                        dialog!!.cancel()
                    }
                    builder.create().show()
                    addToDisposable(
                        Completable.fromAction {
                            viewModel.firstRun()
                        }.subscribeBy(onComplete = {
                            prefs.edit().putBoolean(prefsSettingName, false).apply()
                            firstRun = false
                            publishSubject.onNext(firstRun)
                        })
                    )
                }
                builderChoice.create().show()
            }
            builderPrivacy.setNegativeButton(App.instance.getString(R.string.privacyDecline)) { privacyDialog, _ ->
                privacyDialog.cancel()
                activity!!.finish()
            }
            builderPrivacy.create().show()
        } else {
            //выводим рекламу
            showAd(binding.bottomBanner, adType)
            //иначе просто выводим подсказку мэин страницы
            showHint(view)
            publishSubject.onNext(firstRun)
        }

        //проверяем не превышают ли полученные числа максимальное число
        val maxLengthAmount = BigDecimal("9999999.99")
        var countShowDialog = 0
        addToDisposable(
            loadSettingUseCase.load(Setting("5", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        binding.balanceTotalAmount.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (BigDecimal(s.toString()) > maxLengthAmount || BigDecimal(s.toString()) < -maxLengthAmount) {
                                    if (countShowDialog == 0) {
                                        showDialog(view, R.string.balanceOvercomeTitle)
                                        countShowDialog++
                                    }
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }

                        })
                        binding.expensesTotalAmount.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (BigDecimal(s.toString()) > maxLengthAmount || BigDecimal(s.toString()) < -maxLengthAmount) {
                                    if (countShowDialog == 0) {
                                        showDialog(view, R.string.expensesOvercomeTitle)
                                        countShowDialog++
                                    }
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }

                        })
                        binding.debtsTotalAmount.addTextChangedListener(object : TextWatcher {
                            override fun afterTextChanged(s: Editable?) {
                                if (BigDecimal(s.toString()) > maxLengthAmount || BigDecimal(s.toString()) < -maxLengthAmount) {
                                    if (countShowDialog == 0) {
                                        showDialog(view, R.string.debtsOvercomeTitle)
                                        countShowDialog++
                                    }
                                }
                            }

                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            }

                        })
                    }
                }
        )
    }

    //показываем подсказку
    private fun showHint(view: View) {
        addToDisposable(
            loadSettingUseCase.load(Setting("4", 0))
                .subscribeBy {
                    if (it.value == 1) {
                        val dialog = Dialog(view.context)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.dialog_show_hint)
                        val titleTextView = dialog.findViewById<TextView>(R.id.hintsTitle)
                        titleTextView.setText(R.string.hintMainTitle)
                        val messageTextView = dialog.findViewById<TextView>(R.id.hintsMessage)
                        messageTextView.setText(R.string.hintMainMessage)
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
    }

    //показываем рекламу
    private fun showAd(adView: AdView, type: String?) {
        if (type == "npa") {
            val extras = Bundle()
            extras.putString("npa", "1")

            val adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()
            adView.loadAd(adRequest)
        } else {
            val adRequest = AdRequest.Builder().build()
            adView.loadAd(adRequest)
        }
    }

    //показываем предупреждение
    private fun showDialog(view: View, stringId: Int) {
        val dialog = Dialog(view.context)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_show_warning)
        val messageTextView = dialog.findViewById<TextView>(R.id.warningMessage)
        messageTextView.setText(stringId)
        val checkBox = dialog.findViewById<CheckBox>(R.id.warningCheck)
        val cancelButton = dialog.findViewById<Button>(R.id.cancelButton)
        cancelButton.visibility = View.INVISIBLE
        val positiveButton = dialog.findViewById<Button>(R.id.positiveButton)
        positiveButton.setText(R.string.dialogButtonOk)
        positiveButton.setOnClickListener {
            if (checkBox.isChecked) {
                addToDisposable(
                    saveSettingUseCase.save(Setting("5", 0))
                        .subscribeBy()
                )
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}