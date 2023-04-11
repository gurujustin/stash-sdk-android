/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.product

import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class ProductModule {
    @Binds
    abstract fun bind(source: ProductRepositoryImpl): ProductRepository
}