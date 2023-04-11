/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data

import androidx.room.withTransaction

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
interface DatabaseTransactionRunner {
    suspend operator fun <T> invoke(block: suspend () -> T): T
}

class RoomTransactionRunner(private val db: SampleDatabase) : DatabaseTransactionRunner {
    override suspend operator fun <T> invoke(block: suspend () -> T): T {
        return db.withTransaction {
            block()
        }
    }
}