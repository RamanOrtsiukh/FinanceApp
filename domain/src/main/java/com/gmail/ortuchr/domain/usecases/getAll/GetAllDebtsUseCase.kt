package com.gmail.ortuchr.domain.usecases.getAll

import com.gmail.ortuchr.domain.entity.Debt
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllDebtsUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getAllDebts(type: String) : Flowable<List<Debt>> {
        return repository.getAllDebts(type)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}