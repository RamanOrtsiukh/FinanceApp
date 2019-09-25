package com.gmail.ortuchr.domain.usecases.getSym

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import java.math.BigDecimal
import javax.inject.Inject

class GetSymExpensesCardUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getSymExpensesCard(fromDate: String, toDate: String) : Flowable<BigDecimal> {
        return repository.getSymExpensesCard(fromDate, toDate)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}