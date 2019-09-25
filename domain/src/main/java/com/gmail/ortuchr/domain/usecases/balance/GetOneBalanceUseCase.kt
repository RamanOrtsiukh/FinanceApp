package com.gmail.ortuchr.domain.usecases.balance

import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetOneBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun get(balanceGet: Balance) : Flowable<Balance> {
        return repository.getOneBalance(balanceGet)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}