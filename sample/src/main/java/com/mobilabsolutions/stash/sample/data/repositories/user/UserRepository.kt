/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.user

import com.mobilabsolutions.stash.sample.data.entities.User
import kotlinx.coroutines.flow.Flow

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
interface UserRepository {
    fun observerUser(): Flow<User>
}