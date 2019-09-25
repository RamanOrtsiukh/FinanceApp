package com.gmail.ortuchr.domain.usecases.getSym

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import java.math.BigDecimal
import javax.inject.Inject

class GetSymBalanceCardUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getSymBalanceCard(fromDate: String, toDate: String) : Flowable<BigDecimal> {
        return repository.getSymBalanceCard(fromDate, toDate)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}