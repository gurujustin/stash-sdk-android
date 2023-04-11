/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.user

import com.mobilabsolutions.stash.sample.data.entities.ErrorResult
import com.mobilabsolutions.stash.sample.data.entities.Success
import com.mobilabsolutions.stash.sample.util.AppCoroutineDispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val dispatchers: AppCoroutineDispatchers,
    private val localUserStore: LocalUserStore,
    private val remoteUserDataSource: RemoteUserDataSource
) : UserRepository {

    init {
        GlobalScope.launch(dispatchers.io) {
            if (localUserStore.getUserCount() < 1) {
                createUser()
            }
        }
    }

    override fun observerUser() = localUserStore.observerUser()

    private suspend fun createUser() = coroutineScope {
        val remoteJob = async(dispatchers.io) { remoteUserDataSource.createUser() }
        when (val result = remoteJob.await()) {
            is Success -> localUserStore.saveUser(result.data)
            is ErrorResult -> throw result.exception
        }
    }
}