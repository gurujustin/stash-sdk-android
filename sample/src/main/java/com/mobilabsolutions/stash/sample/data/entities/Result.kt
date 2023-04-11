/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.entities

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */

sealed class Result<T> {
    open fun get(): T? = null
}

data class Success<T>(val data: T) : Result<T>()

data class ErrorResult<T>(val exception: Exception) : Result<T>()