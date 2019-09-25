package com.gmail.ortuchr.finance.presentation.screen

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.crashlytics.android.Crashlytics
import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.databinding.ActivityFinanceBinding
import com.gmail.ortuchr.finance.presentation.base.BaseMvvmActivity
import com.google.android.gms.ads.MobileAds
import io.fabric.sdk.android.Fabric

class FinanceActivity: BaseMvvmActivity<
        FinanceViewModel,
        FinanceRouter,
        ActivityFinanceBinding>() {

    override fun provideViewModel(): FinanceViewModel {
        return ViewModelProviders.of(this)
            .get(FinanceViewModel::class.java)
    }
    override fun provideRouter(): FinanceRouter {
        return FinanceRouter(this)
    }

    override fun provideLayoutId(): Int = R.layout.activity_finance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        MobileAds.initialize(this, getString(R.string.id_app))
        router.goToMain()
    }

}