/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.mappers

import com.mobilabsolutions.stash.sample.data.entities.User
import com.mobilabsolutions.stash.sample.network.response.CreateUserResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Singleton
class CreateUserResponseToUser @Inject constructor() : Mapper<CreateUserResponse, User> {
    override suspend fun map(from: CreateUserResponse): User {
        return User(userId = from.userId)
    }
}