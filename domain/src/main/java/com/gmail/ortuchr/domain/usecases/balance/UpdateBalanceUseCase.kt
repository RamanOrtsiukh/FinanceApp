package com.gmail.ortuchr.domain.usecases.balance

import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class UpdateBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun update(balanceUpdate: Balance) : Completable {
        return repository.updateBalance(balanceUpdate)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}