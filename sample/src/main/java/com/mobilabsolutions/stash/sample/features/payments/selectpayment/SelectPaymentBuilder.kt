/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments.selectpayment

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SelectPaymentBuilder {
    @ContributesAndroidInjector
    abstract fun bindSelectPaymentFragment(): SelectPaymentFragment
}