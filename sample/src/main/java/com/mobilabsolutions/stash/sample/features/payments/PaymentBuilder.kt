/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.payments

import com.mobilabsolutions.stash.sample.features.payments.selectpayment.SelectPaymentBuilder
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class PaymentBuilder {
    @ContributesAndroidInjector(modules = [
        SelectPaymentBuilder::class
    ])
    abstract fun paymentActivity(): PaymentActivity
}