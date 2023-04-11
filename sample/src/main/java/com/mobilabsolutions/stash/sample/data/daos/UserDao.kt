/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.daos

import androidx.room.Dao
import androidx.room.Query
import com.mobilabsolutions.stash.sample.data.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Dao
abstract class UserDao : EntityDao<User> {
    @Query("SELECT COUNT(*) FROM user")
    abstract suspend fun userCount(): Int

    @Query("SELECT * FROM user")
    abstract fun entryObservable(): Flow<User>
}