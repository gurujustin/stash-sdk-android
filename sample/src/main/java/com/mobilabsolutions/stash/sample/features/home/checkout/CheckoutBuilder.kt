/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.checkout

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class CheckoutBuilder {
    @ContributesAndroidInjector
    abstract fun bindCheckoutFragment(): CheckoutFragment
}