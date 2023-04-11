/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.daos

import com.mobilabsolutions.stash.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.stash.sample.data.entities.SampleEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
@Singleton
class EntityInserter @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner
) {
    suspend fun <E : SampleEntity> insertOrUpdate(dao: EntityDao<E>, entities: List<E>) = transactionRunner {
        entities.forEach {
            insertOrUpdate(dao, it)
        }
    }

    suspend fun <E : SampleEntity> insertOrUpdate(dao: EntityDao<E>, entity: E): Long {
        return when {
            entity.id == 0L -> dao.insert(entity)
            else -> {
                dao.update(entity)
                entity.id
            }
        }
    }
}