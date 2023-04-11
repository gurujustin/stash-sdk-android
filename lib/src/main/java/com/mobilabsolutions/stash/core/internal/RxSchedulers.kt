package com.mobilabsolutions.stash.core.internal

import io.reactivex.Scheduler

data class RxSchedulers(
    val io: Scheduler,
    val computation: Scheduler,
    val main: Scheduler
)