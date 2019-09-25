package com.gmail.ortuchr.domain.usecases.debt

import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetOneDebtUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun get(debtGet: Debt) : Flowable<Debt> {
        return repository.getOneDebt(debtGet)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}