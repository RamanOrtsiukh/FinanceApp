package com.gmail.ortuchr.domain.usecases.getSym

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import java.math.BigDecimal
import javax.inject.Inject

class GetSymBalanceFromListUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getSymBalanceFromList(fromDate: String, toDate: String, list: List<String>) : Flowable<BigDecimal> {
        return repository.getSymBalanceFromList(fromDate, toDate, list)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}