package com.gmail.ortuchr.finance.inject

import android.content.Context
import com.gmail.ortuchr.data.local.dao.FinanceDao
import com.gmail.ortuchr.data.local.database.AppDatabase
import com.gmail.ortuchr.data.repositories.FinanceRepositoryImpl
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.finance.executor.UIThread
import dagger.Module
import dagger.Provides

@Module
class AppModule(val context: Context) {

    @Provides
    fun providePostExecutorThread(): PostExecutorThread = UIThread()

    @Provides
    fun provideFinanceRepository(financeDao: FinanceDao): FinanceRepository = FinanceRepositoryImpl(financeDao)

    @Provides
    fun provideFinanceDao(appDatabase: AppDatabase): FinanceDao = appDatabase.getFinanceDao()

    @Provides
    fun provideAppDatabase(context: Context): AppDatabase = AppDatabase.getInstance(context)

    @Provides
    fun provideContext(): Context = context
}