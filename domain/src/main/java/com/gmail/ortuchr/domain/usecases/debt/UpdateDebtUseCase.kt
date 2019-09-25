package com.gmail.ortuchr.domain.usecases.debt

import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class UpdateDebtUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun update(debtUpdate: Debt) : Completable {
        return repository.updateDebt(debtUpdate)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}