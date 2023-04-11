/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.daos

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.mobilabsolutions.stash.sample.data.entities.SampleEntity

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 12-04-2019.
 */
interface EntityDao<in E : SampleEntity> {
    @Insert
    suspend fun insert(entity: E): Long

    @Insert
    suspend fun insertAll(vararg entity: E)

    @Insert
    suspend fun insertAll(entities: List<E>)

    @Update
    suspend fun update(entity: E)

    @Delete
    suspend fun delete(entity: E): Int
}