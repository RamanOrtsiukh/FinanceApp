package com.gmail.ortuchr.domain.usecases.data

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class DeleteDataUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun delete() : Completable {
        return repository.deleteAllBalance()
            .mergeWith(repository.deleteAllDebts())
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}