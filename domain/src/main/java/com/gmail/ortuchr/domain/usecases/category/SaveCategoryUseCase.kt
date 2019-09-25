package com.gmail.ortuchr.domain.usecases.category

import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SaveCategoryUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun save(categorySave: Category) : Completable {
        return repository.saveCategory(categorySave)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}