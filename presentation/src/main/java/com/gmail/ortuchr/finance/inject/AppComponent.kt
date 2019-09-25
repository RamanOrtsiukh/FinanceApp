package com.gmail.ortuchr.finance.inject

import com.gmail.ortuchr.finance.presentation.screen.balance_more.FinanceBalanceMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.balance_more.FinanceBalanceMoreViewModel
import com.gmail.ortuchr.finance.presentation.screen.balance_new.FinanceBalanceNewFragment
import com.gmail.ortuchr.finance.presentation.screen.balance_new.FinanceBalanceNewViewModel
import com.gmail.ortuchr.finance.presentation.screen.debts_more.FinanceDebtsMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.debts_more.FinanceDebtsMoreViewModel
import com.gmail.ortuchr.finance.presentation.screen.debts_new.FinanceDebtsNewFragment
import com.gmail.ortuchr.finance.presentation.screen.debts_new.FinanceDebtsNewViewModel
import com.gmail.ortuchr.finance.presentation.screen.expenses_more.FinanceExpensesMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.expenses_more.FinanceExpensesMoreViewModel
import com.gmail.ortuchr.finance.presentation.screen.expenses_new.FinanceExpensesNewFragment
import com.gmail.ortuchr.finance.presentation.screen.expenses_new.FinanceExpensesNewViewModel
import com.gmail.ortuchr.finance.presentation.screen.main.FinanceMainFragment
import com.gmail.ortuchr.finance.presentation.screen.main.FinanceMainViewModel
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(viewModel: FinanceMainViewModel)
    fun inject(viewModel: FinanceBalanceNewViewModel)
    fun inject(viewModel: FinanceBalanceMoreViewModel)
    fun inject(viewModel: FinanceExpensesNewViewModel)
    fun inject(viewModel: FinanceExpensesMoreViewModel)
    fun inject(viewModel: FinanceDebtsNewViewModel)
    fun inject(viewModel: FinanceDebtsMoreViewModel)
    fun inject(fragment: FinanceMainFragment)
    fun inject(fragment: FinanceBalanceNewFragment)
    fun inject(fragment: FinanceBalanceMoreFragment)
    fun inject(fragment: FinanceExpensesNewFragment)
    fun inject(fragment: FinanceExpensesMoreFragment)
    fun inject(fragment: FinanceDebtsNewFragment)
    fun inject(fragment: FinanceDebtsMoreFragment)
}