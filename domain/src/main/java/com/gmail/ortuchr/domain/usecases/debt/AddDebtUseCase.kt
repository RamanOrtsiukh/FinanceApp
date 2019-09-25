package com.gmail.ortuchr.domain.usecases.debt

import com.gmail.ortuchr.domain.entity.DebtAdd
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class AddDebtUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun add(debtAdd: DebtAdd) : Completable {
        return repository.addDebt(debtAdd)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}