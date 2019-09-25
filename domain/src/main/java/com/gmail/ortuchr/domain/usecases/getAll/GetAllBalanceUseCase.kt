package com.gmail.ortuchr.domain.usecases.getAll

import com.gmail.ortuchr.domain.entity.Balance
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class GetAllBalanceUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun getAllBalance(fromDate: String, toDate: String, list: List<String>) : Flowable<List<Balance>> {
        return repository.getAllBalance(fromDate, toDate, list)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}