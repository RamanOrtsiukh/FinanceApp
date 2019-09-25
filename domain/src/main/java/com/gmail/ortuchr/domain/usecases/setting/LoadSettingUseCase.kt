package com.gmail.ortuchr.domain.usecases.setting

import com.gmail.ortuchr.domain.entity.Setting
import com.gmail.ortuchr.domain.executor.PostExecutorThread
import com.gmail.ortuchr.domain.repositories.FinanceRepository
import com.gmail.ortuchr.domain.usecases.BaseUseCase
import io.reactivex.Flowable
import javax.inject.Inject

class LoadSettingUseCase @Inject constructor(
    postExecutorThread: PostExecutorThread,
    private val repository: FinanceRepository
) : BaseUseCase(postExecutorThread) {

    fun load(settingLoad: Setting) : Flowable<Setting> {
        return repository.loadSetting(settingLoad)
            .observeOn(postExecutorThread)
            .subscribeOn(workExecutorThread)
    }
}