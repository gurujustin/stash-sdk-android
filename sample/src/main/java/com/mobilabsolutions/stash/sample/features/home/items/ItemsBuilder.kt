/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.sample.features.home.items

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">yisuk</a>
 */
@Module
abstract class ItemsBuilder {
    @ContributesAndroidInjector
    abstract fun bindItemsFragment(): ItemsFragment
}