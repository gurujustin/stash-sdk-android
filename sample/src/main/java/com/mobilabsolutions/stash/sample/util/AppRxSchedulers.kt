/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.util

import io.reactivex.Scheduler

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
data class AppRxSchedulers(
    val io: Scheduler,
    val computation: Scheduler,
    val main: Scheduler
)