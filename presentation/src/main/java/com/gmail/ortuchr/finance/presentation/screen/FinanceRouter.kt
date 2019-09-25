package com.gmail.ortuchr.finance.presentation.screen

import com.gmail.ortuchr.finance.R
import com.gmail.ortuchr.finance.presentation.base.BaseRouter
import com.gmail.ortuchr.finance.presentation.screen.balance_more.FinanceBalanceMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.balance_new.FinanceBalanceNewFragment
import com.gmail.ortuchr.finance.presentation.screen.debts_more.FinanceDebtsMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.debts_new.FinanceDebtsNewFragment
import com.gmail.ortuchr.finance.presentation.screen.expenses_more.FinanceExpensesMoreFragment
import com.gmail.ortuchr.finance.presentation.screen.expenses_new.FinanceExpensesNewFragment
import com.gmail.ortuchr.finance.presentation.screen.main.FinanceMainFragment

class FinanceRouter(activity: FinanceActivity) :
    BaseRouter<FinanceActivity>(activity) {

    fun goToMain() {
       replaceFragment(
           activity.supportFragmentManager,
           FinanceMainFragment.getInstance(),
           R.id.container,
           false)
    }

    fun goToBalanceMore() {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceBalanceMoreFragment.getInstance(),
            R.id.container)
    }

    fun goToBalanceNew(id: String, symCard: String, symCash: String, existAmount: String, account: String) {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceBalanceNewFragment.getInstance(id, symCard, symCash, existAmount, account),
            R.id.container)
    }

    fun goToDebtsMore() {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceDebtsMoreFragment.getInstance(),
            R.id.container)
    }

    fun goToDebtsNew(id: String, symCard: String, symCash: String, existAmount: String, account: String, type: String) {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceDebtsNewFragment.getInstance(id, symCard, symCash, existAmount, account, type),
            R.id.container)
    }

    fun goToExpensesMore() {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceExpensesMoreFragment.getInstance(),
            R.id.container)
    }

    fun goToExpensesNew(id: String, symCard: String, symCash: String, existAmount: String, account: String) {
        replaceFragment(
            activity.supportFragmentManager,
            FinanceExpensesNewFragment.getInstance(id, symCard, symCash, existAmount, account),
            R.id.container)
    }
}