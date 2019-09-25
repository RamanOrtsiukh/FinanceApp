package com.gmail.ortuchr.domain.usecases.category

import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class LoadCategoryUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun load(categoryLoad: Category) : Flowable<Category> {
        return repository.loadCategory(categoryLoad)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}