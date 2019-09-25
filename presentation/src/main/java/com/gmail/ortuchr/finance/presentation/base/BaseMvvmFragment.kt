package com.gmail.ortuchr.finance.presentation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.gmail.ortuchr.finance.BR
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseMvvmFragment<VM: BaseViewModel<R>,
        R: BaseRouter<*>,
        B: ViewDataBinding>: BaseFragment() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: B
    protected var router: R? = null

    abstract fun provideViewModel(): VM
    abstract fun provideLayoutId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = provideViewModel()
        binding = DataBindingUtil.inflate(inflater, provideLayoutId(),
            container, false)
        binding.setVariable(BR.viewModel, viewModel)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity is BaseMvvmActivity<*,*,*>) {
            router = (activity as BaseMvvmActivity<*,*,*>).router as R
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.addRouter(router)
    }

    override fun onPause() {
        super.onPause()
        viewModel.removeRouter()
    }

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    protected fun addToDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }
}