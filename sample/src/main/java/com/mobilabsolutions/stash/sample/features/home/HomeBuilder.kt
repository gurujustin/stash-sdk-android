package com.mobilabsolutions.stash.sample.features.home

import android.content.Context
import com.mobilabsolutions.stash.sample.features.home.checkout.CheckoutBuilder
import com.mobilabsolutions.stash.sample.features.home.info.InfoBuilder
import com.mobilabsolutions.stash.sample.features.home.items.ItemsBuilder
import com.mobilabsolutions.stash.sample.features.home.paymentmethods.PaymentMethodsBuilder
import com.mobilabsolutions.stash.sample.inject.PerActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 16-08-2019.
 */
@Module
abstract class HomeBuilder {
    @ContributesAndroidInjector(modules = [
        HomeModuleBinds::class,
        ItemsBuilder::class,
        CheckoutBuilder::class,
        PaymentMethodsBuilder::class,
        InfoBuilder::class
    ])
    abstract fun homeActivity(): HomeActivity
}

@Module
abstract class HomeModuleBinds {
    @Binds
    @PerActivity
    abstract fun bindContext(activity: HomeActivity): Context
}