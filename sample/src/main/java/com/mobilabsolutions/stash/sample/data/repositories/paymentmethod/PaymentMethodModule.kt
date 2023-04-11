/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.data.repositories.paymentmethod

import dagger.Binds
import dagger.Module

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class PaymentMethodModule {
    @Binds
    abstract fun bind(source: PaymentMethodRepositoryImpl): PaymentMethodRepository
}