/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.user

import com.mobilabsolutions.stash.sample.data.DatabaseTransactionRunner
import com.mobilabsolutions.stash.sample.data.daos.EntityInserter
import com.mobilabsolutions.stash.sample.data.daos.UserDao
import com.mobilabsolutions.stash.sample.data.entities.User
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class LocalUserStore @Inject constructor(
    private val transactionRunner: DatabaseTransactionRunner,
    private val entityInserter: EntityInserter,
    private val userDao: UserDao
) {
    suspend fun getUserCount() = userDao.userCount()

    fun observerUser() = userDao.entryObservable()

    suspend fun saveUser(user: User) = transactionRunner {
        entityInserter.insertOrUpdate(userDao, user)
    }
}