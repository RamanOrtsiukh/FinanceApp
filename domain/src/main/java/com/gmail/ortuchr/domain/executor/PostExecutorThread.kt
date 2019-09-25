package com.gmail.ortuchr.domain.executor

import io.reactivex.Scheduler

interface PostExecutorThread {
    fun getScheduler(): Scheduler
}