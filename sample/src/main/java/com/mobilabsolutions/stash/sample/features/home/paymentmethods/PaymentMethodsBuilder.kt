/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.paymentmethods

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 08-04-2019.
 */
@Module
abstract class PaymentMethodsBuilder {
    @ContributesAndroidInjector
    abstract fun bindPaymentFragment(): PaymentMethodsFragment
}