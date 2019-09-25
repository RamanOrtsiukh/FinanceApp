package com.gmail.ortuchr.finance.executor

import com.gmail.ortuchr.domain.executor.PostExecutorThread
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers

class UIThread: PostExecutorThread {
    override fun getScheduler(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}