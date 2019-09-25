package com.gmail.ortuchr.domain.usecases.setting

import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Completable
import javax.inject.Inject

class SaveSettingUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun save(settingSave: Setting) : Completable {
        return repository.saveSetting(settingSave)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}