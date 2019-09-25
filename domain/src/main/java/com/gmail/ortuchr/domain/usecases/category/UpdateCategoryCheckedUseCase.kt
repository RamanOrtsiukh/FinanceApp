package com.gmail.ortuchr.domain.usecases.category

import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class UpdateCategoryCheckedUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun update(categoryUpdate: Category) : Completable {
        return repository.updateCategoryChecked(categoryUpdate)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}