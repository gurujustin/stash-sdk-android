/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.user

import com.mobilabsolutions.stash.sample.data.entities.Result
import com.mobilabsolutions.stash.sample.data.entities.User
import com.mobilabsolutions.stash.sample.data.mappers.CreateUserResponseToUser
import com.mobilabsolutions.stash.sample.data.mappers.toLambda
import com.mobilabsolutions.stash.sample.extensions.toResult
import com.mobilabsolutions.stash.sample.network.SampleMerchantService
import javax.inject.Inject

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
class RemoteUserDataSource @Inject constructor(
    private val sampleMerchantService: SampleMerchantService,
    private val createUserResponseToUser: CreateUserResponseToUser
) {

    suspend fun createUser(): Result<User> {
        return sampleMerchantService.createUser().execute()
            .toResult(createUserResponseToUser.toLambda())
    }
}