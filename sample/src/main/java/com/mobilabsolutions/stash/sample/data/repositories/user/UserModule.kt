/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.user

import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-04-2019.
 */
@Module
abstract class UserModule {
    @Binds
    abstract fun bind(source: UserRepositoryImpl): UserRepository
}