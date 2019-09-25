package com.gmail.ortuchr.domain.usecases.category

import com.gmail.ortuchr.domain.entity.Category
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class ChangeCategoryInRecordsUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun change(categoryPrevious: Category, categoryChange: Category) : Completable {
        return repository.changeCategoryInRecords(categoryPrevious, categoryChange)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}