package com.gmail.ortuchr.domain.usecases.debt

import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class DeleteDebtUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun delete(debtDelete: Debt) : Completable {
        return repository.deleteDebt(debtDelete)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}