package com.gmail.ortuchr.finance.presentation.base

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel<R: BaseRouter<*>>: ViewModel() {

    protected var router: R? = null

    fun addRouter(router: R?) {
        this.router = router
    }

    fun removeRouter() {
        router = null
    }

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    protected fun addToDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

    fun goBack() {
        router?.goBack()
    }
}