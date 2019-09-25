package com.gmail.ortuchr.domain.usecases.balance

import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class DeleteBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun delete(balanceDelete: Balance) : Completable {
        return repository.deleteBalance(balanceDelete)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}