package com.gmail.ortuchr.finance.app

import android.content.Context
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.gmail.ortuchr.finance.inject.AppComponent
import com.gmail.ortuchr.finance.inject.AppModule
import com.gmail.ortuchr.finance.inject.DaggerAppComponent
import com.gmail.ortuchr.finance.presentation.utils.TypefaceUtil

class App: MultiDexApplication() {

    companion object {
        lateinit var instance: App
        @JvmStatic lateinit var appComponent: AppComponent
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        TypefaceUtil.overrideFont(this, "SERIF", "fonts/Roboto-Regular.ttf")

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}