/*
 * Copyright © MobiLab Solutions GmbH
 */

package com.mobilabsolutions.stash.adyen

import com.mobilabsolutions.stash.core.internal.SslSupportModule
import com.mobilabsolutions.stash.core.internal.StashComponent
import com.mobilabsolutions.stash.core.internal.StashModule
import dagger.Component
import javax.inject.Singleton

/**
 * @author <a href="yisuk@mobilabsolutions.com">Yisuk Kim</a> on 30-05-2019.
 */
@Singleton
@Component(modules = [
    StashModule::class,
    AdyenModule::class,
    SslSupportModule::class
])
internal interface AdyenIntegrationTestComponent : StashComponent {
    fun injectTest(test: AdyenIntegrationTest)
}