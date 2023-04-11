package com.mobilabsolutions.stash.sample.features.home.info

import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 26-08-2019.
 */
@Module
abstract class InfoBuilder {
    @ContributesAndroidInjector
    abstract fun infoFragment(): InfoFragment
}