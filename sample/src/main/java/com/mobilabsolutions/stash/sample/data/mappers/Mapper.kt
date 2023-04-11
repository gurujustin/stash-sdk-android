/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.mappers

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface Mapper<F, T> {
    suspend fun map(from: F): T
}

fun <F, T> Mapper<F, T>.toLambda(): suspend (F) -> T {
    return { map(it) }
}