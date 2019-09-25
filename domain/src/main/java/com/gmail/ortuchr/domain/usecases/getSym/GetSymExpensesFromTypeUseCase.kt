package com.gmail.ortuchr.domain.usecases.getSym

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import java.math.BigDecimal
import javax.inject.Inject

class GetSymExpensesFromTypeUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getSymExpensesFromType(fromDate: String, toDate: String, type: String) : Flowable<BigDecimal> {
        return repository.getSymExpensesFromType(fromDate, toDate, type)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}