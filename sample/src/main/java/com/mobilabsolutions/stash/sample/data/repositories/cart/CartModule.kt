/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.cart

import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class CartModule {
    @Binds
    abstract fun bind(source: CartRepositoryImpl): CartRepository
}