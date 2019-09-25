package com.gmail.ortuchr.domain.usecases.balance

import com.gmail.ortuchr.domain.entity.BalanceAdd
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class AddBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun add(balanceAdd: BalanceAdd) : Completable {
        return repository.addBalance(balanceAdd)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}