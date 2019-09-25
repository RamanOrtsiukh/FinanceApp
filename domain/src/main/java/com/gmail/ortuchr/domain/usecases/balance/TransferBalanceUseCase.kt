package com.gmail.ortuchr.domain.usecases.balance

import com.gmail.ortuchr.domain.entity.BalanceAdd
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class TransferBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun transfer(balanceFrom: BalanceAdd, balanceTo: BalanceAdd) : Completable {
        return repository.addBalance(balanceFrom)
            .mergeWith(repository.addBalance(balanceTo))
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}